package com.example.weatherapp.data

data class WeatherResponse(
    val name: String,           // city name - matches "name" key in JSON
    val main: Main,             // nested object -- contains temp and humidity
    val weather: List<Weather>, // array of weather conditions
    val wind: Wind              // nested object containing wind speed
)

data class Main(
    val temp: Double, // current temp
    val humidity: Int // humidity percentage
)

data class Weather(
    val description: String
)

data class Wind(
    val speed: Double // wind speed in meters per second
)