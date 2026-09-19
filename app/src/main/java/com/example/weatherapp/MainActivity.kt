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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                // This block runs every time the "Get Weather" button is tapped
                val trimmedCity = city.trim()
                // .trim() removes spaces typed at the start or end

                if (trimmedCity.isEmpty()) {
                    // .isEmpty() = true if the string has 0 characters
                    Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()
                } else {
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
            Text("Get Weather")
        }

        Text(cityText, modifier = Modifier.padding(top = 24.dp))
        Text(temperatureText, modifier = Modifier.padding(top = 8.dp))
        Text(descriptionText, modifier = Modifier.padding(top = 8.dp))
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


















