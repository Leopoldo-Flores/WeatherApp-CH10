package com.example.weatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.network.AppConstants
import com.example.weatherapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers  // thread types: Main (UI), IO (network/disk)
import kotlinx.coroutines.launch       // .launch {} starts a coroutine
import kotlinx.coroutines.withContext  // switches thread context inside a coroutine
// class 2
import android.util.Log
// class 3
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import com.example.weatherapp.data.FeedbackRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    WeatherScreen()
                }
            }
        }
    }
}

@Composable
fun WeatherScreen() {
    var city by remember { mutableStateOf("") }
    var cityText by remember { mutableStateOf("City: --") }
    var temperatureText by remember { mutableStateOf("Temperature: --") }
    var descriptionText by remember { mutableStateOf("Description: --") }
    var windResult by remember { mutableStateOf("Wind Speed: --") }
    var humidityResult by remember { mutableStateOf("Humidity: --") }
    var isLoading by remember { mutableStateOf(false) }
    // Post
    var currentCity by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var comment by remember { mutableStateOf("") }
    var feedbackResult by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            enabled = isLoading, // disables the button mid-request - stops duplicate calls
            onClick = {
                // This block runs every time the "Get Weather" button is tapped
                val trimmedCity = city.trim()
                // .trim() removes spaces typed at the start or end

                if (trimmedCity.isEmpty()) {
                    // .isEmpty() = true if the string has 0 characters
                    Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    scope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.weatherApiService.getWeather(
                                    city = trimmedCity,
                                    apiKey = AppConstants.API_KEY,
                                    units = AppConstants.UNITS
                                )
                            } // response.raw() gives the underlying OkHttp response
                              // request.url is the exact URL Retrofit built. can check in LOGCAT
                            Log.d("WeatherApp", "Request URL: ${response.raw().request.url}")
                            Log.d("WeatherApp", "Request code: ${response.code()}")

                            if (response.isSuccessful) {
                                val weather = response.body()
                                if (weather != null) {
                                    city = "City: ${weather.name}"
                                    temperatureText = "Temperature: ${weather.main.temp}"
                                    descriptionText = "Description: ${weather.weather[0].description}"
                                    windResult = "Wind Speed: ${weather.wind.speed} MPH"
                                    humidityResult = "Humidity: ${weather.main.humidity}%"
                                    currentCity = trimmedCity
                                    // Steps to complete:
                                    // 1. Set windResult from weather.wind.speed (append "MPH")
                                    // 2. Set humidityResult from weather.main.humidity (append "%")
                                    // 3. Below in the else branch, replace the single Toast with a
                                    //    when (response.code()) --
                                }
                            } else{
                                // This Toast gets replaced by step
                                // with different messages for 404, 401, and anything else (401- invalid key)
                                when (response.code()) {
                                    404 -> Toast.makeText(context, "City not found. Check the name and try again.", Toast.LENGTH_SHORT).show()
                                    401 -> Toast.makeText(context, "Invalid API key. Check AppConstants.kt.", Toast.LENGTH_SHORT).show()
                                    else ->Toast.makeText(context, "Something went wrong (code ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Network error. Check your connection.", Toast.LENGTH_SHORT).show()
                        }
                        finally {
                            isLoading = false
                        }
                    }
                    fetchWeather(trimmedCity, context) { c, t, d ->
                        cityText = c
                        temperatureText = t
                        descriptionText = d
                    }
                }
            },
            modifier =
                Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(if (isLoading) "Loading.." else "Get Weather")
        }

        Text(cityText, fontSize = 20.sp, modifier = Modifier.padding(top = 24.dp))
        Text(temperatureText, fontSize = 18.sp,  modifier = Modifier.padding(top = 8.dp))
        Text(descriptionText, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(windResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(humidityResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))

        HorizontalDivider(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp))

        Text("How do you feel about today's weather?", fontSize = 16.sp)

        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toInt() },
            valueRange = 1f..5f,
            steps = 3 // stops between 1 and 5 - 5 total selectable whole numbers
        )
        Text("Rating: $rating/5") // slider has no built-in label

        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Leave a comment about the weather...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (currentCity.isEmpty()) {
                    Toast.makeText(context, "Please fetch a weather for a city first", Toast.LENGTH_SHORT).show()
                } else if (comment.isBlank()) {
                    Toast.makeText(context, "Please leave a comment", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        try {                             // Gson converts this to JSON
                            val request = FeedbackRequest(city = currentCity, rating = rating, comment = comment)

                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.feedbackApiService.submitFeedback(request)
                            }
                            // ASSIGNMENT 3
                            // 1. If response.isSuccessful: set feedbackResult to a success message, then
                            //    clear the comment field and reset rating back to 3
                            // 2. If NOT successful: set feedbackResult to a failure message

                        } catch (e: Exception) {
                            feedbackResult = "Error submitting feedback. Check your connection."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Submit Feedback")
        }
        Text(feedbackResult, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
    }
}


private fun fetchWeather(
    city: String,
    context: android.content.Context,
    onResult: (city: String,temp: String, desc: String) -> Unit
){
    // =======================================================
    // ASSIGNMENT 1 -- Implement this function!
    // =======================================================
    // Steps to complete:
    // 1. This function needs a CoroutineScope since it's not inside an Activity --
    //    use kotlinx.coroutines.MainScope() or pass one in from the composable
    //    via rememberCoroutineScope() (covered in class)
    // 2. Inside it, use withContext(Dispatchers.IO) { } for the network call
    // 3. Call RetrofitClient.weatherApiService.getWeather(city, AppConstants.API_KEY, AppConstants.UNITS)
    // 4. Check if response.isSuccessful
    // 5. If YES: call onResult(...) with the city, temperature, and description strings
    // 6. If NO: show a Toast "City not found. Check the name and try again."
    // 7. Wrap everything in try { } catch (e: Exception) { } for network errors

    // DELETE this placeholder line when you implement the function:
    Toast.makeText(context, "Coming soon -- weather for $city", Toast.LENGTH_SHORT).show()
}


















