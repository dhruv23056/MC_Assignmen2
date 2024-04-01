# Weather History App

This application is designed to retrieve historical weather data using a free weather API and store it in a database for offline access. The app allows users to input a date and year to get the maximum and minimum temperatures recorded on that date.

## Features

1. **Utilizing the Weather API**:
   - The app accesses a free weather API to download historical weather data in JSON format.

2. **User Interface**:
   - The UI prompts the user to enter a date and year for which they want weather data.
   - The UI displays the maximum and minimum temperatures for the specified date.

3. **Parsing JSON Files**:
   - The app parses the JSON data retrieved from the API to extract temperature information.

4. **Database Integration**:
   - The app creates a database and schema to store weather data locally.
   - It inserts downloaded weather data into the database for future offline access.

5. **Data Handling**:
   - If the user requests weather data for a future date, the app calculates the average temperatures of the last 10 available years.

6. **Network Connectivity**:
   - The app checks for network connectivity before attempting to download data from the API.
   - Proper error handling and messages are displayed if there are issues with network connectivity.

## Usage

1. Install the necessary dependencies (e.g., Python libraries for API requests, SQLite for database operations).
2. Run the app.
3. Enter the desired date and year when prompted.
4. View the maximum and minimum temperatures for the specified date.

## Implementation Details

- The app is written in Python and uses libraries such as `requests` for API requests and `sqlite3` for database operations.
- Error handling is implemented to handle invalid user inputs, network errors, and database operations.
- The app follows a modular structure with clear separation of concerns (e.g., API communication, database handling, UI).

