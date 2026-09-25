package com.example.weatherapp.network
// object in kotlin = a singleton class
object AppConstants {
    // Base URL = the root address of the api
    // IMPORTANT: must end with "/" retrofit requires this
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/"

    const val API_KEY = "e275afad81908887d9b24b05ab98fe6f"

    const val UNITS = "imperial"

    const val FEEDBACK_BASE_URL = "https://mock.apidog.com/m1/1386452-1392954-default/feedback"
}