package com.yarvol.skyvibe.network

import kotlinx.serialization.Serializable

@Serializable
data class CurrentTemperature(
    val current: Temperature
)

@Serializable
data class Temperature(
    val temperature_2m: Double,
    val apparent_temperature: Double
)

@Serializable
data class ApparentTemperature(
    val apparent_temperature: Double
)

@Serializable
data class HourlyWeather(
    val temperature_2m: List<Double>,  // Массив с температурами за каждый час
    val time: List<String>             // Массив с временными метками для каждого часа
)

@Serializable
data class WeatherResponse(
    val hourly: HourlyWeather          // Вложенный объект с почасовыми данными
)