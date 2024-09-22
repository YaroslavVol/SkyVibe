package com.yarvol.skyvibe.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WeatherApi {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }
    }

//    https://api.open-meteo.com/v1/forecast?latitude=55.154&longitude=61.4291&hourly=temperature_2m&forecast_days=1

    suspend fun getWeather(latitude: Double, longitude: Double): WeatherResponse {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"
        return httpClient.get(url).body()
    }

    suspend fun getWeatherData(): HttpResponse {
        return httpClient.get("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current_weather=true").body()
    }

    suspend fun fetchHourlyWeatherData(latitude: Double, longitude: Double): WeatherResponse {
        val url = "https://api.open-meteo.com/v1/forecast"
        return httpClient.get(url) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("hourly", "temperature_2m")
            parameter("timezone", "auto")  // Автоматическое определение часового пояса
            parameter("forecast_days", 1)  // Прогноз на 1 день
        }.body()
    }

    suspend fun getCurrentTemperature(latitude: Double, longitude: Double): CurrentTemperature {
        return httpClient.get("https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,apparent_temperature&timezone=auto&forecast_days=1").body()
    }
}