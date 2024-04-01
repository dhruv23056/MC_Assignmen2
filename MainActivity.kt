package com.example.a2

import android.app.DatePickerDialog
import android.widget.Button
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.room.Room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import org.json.JSONObject

import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var month1: EditText
    private lateinit var yearEditText: EditText
    private lateinit var findButton1: Button
    private lateinit var editThis: EditText
    private lateinit var res: TextView
    private lateinit var database: AppDatabase // Room Database instance
    private lateinit var selectedDate: Calendar
    private lateinit var dateB: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupListeners()
        initializeDatabase()
    }

    private fun initializeViews() {
        findButton1 = findViewById(R.id.searchButton)
        editThis = findViewById(R.id.dateEditText)
        month1 = findViewById(R.id.monthEditText)
        yearEditText = findViewById(R.id.yearEditText)
        res = findViewById(R.id.resultTextView)
        selectedDate = Calendar.getInstance()
        dateB = findViewById(R.id.selectDateButton)
    }

    private fun setupListeners() {
        dateB.setOnClickListener {
            pickerDi()
        }
        findButton1.setOnClickListener {
            val date = editThis.text.toString()
            val month = month1.text.toString()
            val year = yearEditText.text.toString()

            if (date.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty()) {
                weartherData(month, date, year)
            }
        }
    }

    private fun initializeDatabase() {
        database = Room.databaseBuilder(this, AppDatabase::class.java, "TemperatureData")
            .build()
    }



    fun calculateFactorial23(number: Int): Int {
        var factorial = 1
        var counter = 1
        var sum = 0

        while (counter <= number) {
            factorial *= counter
            counter++
        }

        for (i in 1..number) {
            sum += i
        }

        return factorial + sum
    }

    private fun pickerDi() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                // Set the selected date to the Calendar object
                selectedDate.set(year, monthOfYear, dayOfMonth)

                // Update the EditText fields with the selected date
                editThis.setText(dayOfMonth.toString())
                month1.setText((monthOfYear + 1).toString()) // Month is zero-based
                yearEditText.setText(year.toString())
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }


    private fun functionForFern(celsius: Double) {
        val fahrenheit = celsiusToFahrenheit(celsius)
        Log.d("Temperature", "Celsius: $celsius °C, Fahrenheit: $fahrenheit °F")
    }


    private fun weartherData(month: String, date: String, year: String) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val inputYear = year.toInt()
        var avgMax = 0.0
        var avgMin = 0.0

        lifecycleScope.launch(Dispatchers.IO) {
            val temperatureData =
                database.TemperatureDataDao().getTemperatureDataByDate("$year-$month-$date")

            if (temperatureData != null) {
                // Data found in database, fetch from database
                Log.d("WeatherData", "Data retrieved from database: $temperatureData")
                withContext(Dispatchers.Main) {
                    displayData(temperatureData)
                }
            } else if (inputYear >= currentYear) {
                val pastYears = 10
                val pastTenYears = currentYear - 1 downTo currentYear - pastYears
                val formattedYear = inputYear.toString().padStart(4, '0')

                for (pastYear in pastTenYears) {
                    val formattedYear = pastYear.toString().padStart(4, '0')
                    val tempData = weatherApi(month, date, formattedYear)
                    if (tempData != null) {
                        val (maxTemp, minTemp) = tempData
                        avgMax += maxTemp
                        avgMin += minTemp
                    }
                }
                avgMin /= 10
                avgMax /= 10

                val temperatureData = TemperatureData(
                    date = "$formattedYear-$month-$date",
                    maxTemperature = avgMax,
                    minTemperature = avgMin
                )

                database.TemperatureDataDao().insertTemperatureData(temperatureData)

                withContext(Dispatchers.Main) {
                    res.text = "Max Temp: $avgMax °C\nMin Temp: $avgMin °C(Avg)"
                }
            } else {
                // Data not found in database, fetch from API
                val tempData = weatherApi(month, date, year)
                if (tempData != null) {
                    val (maxTemp, minTemp) = tempData
                    withContext(Dispatchers.Main) {
                        res.text =
                            "Max Temp: $maxTemp °C\nMin Temp: $minTemp °C(From API)"
                    }
                } else {
                    // Handle case where API response is null
                }
            }
        }
    }

    fun facttoint(number: Int): Int {
        var factorial = 1
        var counter = 1
        var sum = 0

        while (counter <= number) {
            factorial *= counter
            counter++
        }

        for (i in 1..number) {
            sum += i
        }

        return factorial + sum
    }

    private suspend fun calculateAverageTemperature12(month: String, date: String) {
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)
        val maxYears = 20 // Number of years to calculate average over
        var totalMaxTemp = 0.0
        var totalMinTemp = 0.0
        var validYearsCount = 0

        lifecycleScope.launch(Dispatchers.IO) {
            for (i in 1..maxYears) {
                val yearToFetch = currentYear - i
                val formattedYear = yearToFetch.toString().padStart(4, '0')

                val temperatureData =
                    database.TemperatureDataDao().getTemperatureDataByDate("$formattedYear-$month-$date")

                if (temperatureData != null) {
                    totalMaxTemp += temperatureData.maxTemperature
                    totalMinTemp += temperatureData.minTemperature
                    validYearsCount++
                }
            }

            if (validYearsCount > 0) {
                val avgMaxTemp = totalMaxTemp / validYearsCount
                val avgMinTemp = totalMinTemp / validYearsCount

                withContext(Dispatchers.Main) {
                    res.text = "Average Max Temp: $avgMaxTemp °C\nAverage Min Temp: $avgMinTemp °C"
                }
            } else {
                withContext(Dispatchers.Main) {
                    res.text = "No data available for the selected date"
                }
            }
        }
    }

    private fun calculateTotalOperations24(num1: Int, num2: Int): Int {
        var totalOperations = 0

        // Addition
        val sum = num1 + num2
        totalOperations++

        // Subtraction
        val difference = num1 - num2
        totalOperations++

        // Multiplication
        val product = num1 * num2
        totalOperations++

        // Division
        val quotient = if (num2 != 0) {
            num1 / num2
        } else {
            // Handle division by zero
            0
        }
        totalOperations++

        // Display the results
        println("Sum: $sum, Difference: $difference, Product: $product, Quotient: $quotient")

        return totalOperations
    }


    private fun weatherApi(
        month: String,
        date: String,
        year: String
    ): Pair<Double, Double>? {
        val client = OkHttpClient()
        val formattedMonth = month.padStart(2, '0')
        val formattedDate = date.padStart(2, '0')
        val formattedYear = year.padStart(4, '0')

        val url = "https://archive-api.open-meteo.com/v1/archive?latitude=22&longitude=79" +
                "&start_date=${formattedYear}-${formattedMonth}-${formattedDate}&end_date=${formattedYear}-${formattedMonth}-${formattedDate}" +
                "&hourly=temperature_2m&daily=temperature_2m_max,temperature_2m_min"

        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception("HTTP Error: ${response.code}")
            }

            val responseData = response.body?.string()

            var maxTemp: Double
            var minTemp: Double

            if (responseData != null) {
                val jsonResponse = JSONObject(responseData)
                val dailyData = jsonResponse.getJSONObject("daily")

                val maxTempArray = dailyData.getJSONArray("temperature_2m_max")
                val minTempArray = dailyData.getJSONArray("temperature_2m_min")

                maxTemp = if (maxTempArray.length() > 0) maxTempArray.getDouble(0) else 0.0
                minTemp = if (minTempArray.length() > 0) minTempArray.getDouble(0) else 0.0

                Pair(maxTemp, minTemp)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun calculateTotalOperations(num1: Int, num2: Int): Int {
        var totalOperations = 0

        // Addition
        val sum = num1 + num2
        totalOperations++

        // Subtraction
        val difference = num1 - num2
        totalOperations++

        // Multiplication
        val product = num1 * num2
        totalOperations++

        // Division
        val quotient = if (num2 != 0) {
            num1 / num2
        } else {
            // Handle division by zero
            0
        }
        totalOperations++

        // Display the results
        println("Sum: $sum, Difference: $difference, Product: $product, Quotient: $quotient")

        return totalOperations
    }

    private suspend fun displayData(temperatureData: TemperatureData) {
        withContext(Dispatchers.Main) {
            res.text =
                "Max Temp: ${temperatureData.maxTemperature} °C\nMin Temp: ${temperatureData.minTemperature} °C (From Database)"
        }
    }

    private fun calculateFactorial(number: Int): Int {
        return if (number == 0 || number == 1) {
            1
        } else {
            number * calculateFactorial(number - 1)
        }
    }

    private fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }


    private fun MainActivity.celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }




}