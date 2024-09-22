package com.yarvol.skyvibe.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yarvol.skyvibe.model.City
import com.yarvol.skyvibe.network.CurrentTemperature
import com.yarvol.skyvibe.network.WeatherApi
import kotlinx.coroutines.launch

@Composable
fun CitySelection(modifier: Modifier = Modifier) {
    var isLoading by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<City?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val client = remember { WeatherApi() }
    val scope = rememberCoroutineScope()
    var currentTemperatureData by remember { mutableStateOf<CurrentTemperature?>(null) }

    val cities = listOf(
        // City List
        City("Москва", 55.7558, 37.6173),
        City("Санкт-Петербург", 59.9391, 30.3141),
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.size(width = 250.dp, height = 60.dp)
        ) {
            Text(
                text = selectedCity?.name ?: "Выберите город",
                fontSize = 18.sp
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    onClick = {
                        isLoading = true
                        selectedCity = city
                        scope.launch {
                            try {
                                currentTemperatureData =
                                    client.getCurrentTemperature(city.latitude, city.longitude)
                                errorMessage = null
                            } catch (e: Exception) {
                                errorMessage =
                                    "Не удалось подключиться к серверу: Проверьте подключение к интернету."
                            } finally {
                                isLoading = false
                            }
                        }
                        expanded = false
                    },
                    text = {
                        Text(
                            text = city.name
                        )
                    }
                )
            }
        }
    }
    Spacer(Modifier.padding(12.dp))
    if (errorMessage != null) {
        Text(
            text = "Ошибка: $errorMessage",
            fontSize = 18.sp
        )
    } else if (currentTemperatureData != null) {
        Text(
            text = "Температура в городе сейчас: " +
                    if (currentTemperatureData?.current?.temperature_2m == null) {
                        "0°C"
                    } else {
                        currentTemperatureData?.current?.temperature_2m.toString() + "°C"
                    },
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Ощущаемая температура в городе сейчас: " +
                    if (currentTemperatureData?.current?.apparent_temperature == null) {
                        "0°C"
                    } else {
                        currentTemperatureData?.current?.apparent_temperature.toString() + "°C"
                    },
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
    if (isLoading) {
        CircularProgressIndicator()
    }
}
