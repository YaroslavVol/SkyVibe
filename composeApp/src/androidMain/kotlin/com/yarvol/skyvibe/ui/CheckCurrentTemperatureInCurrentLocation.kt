package com.yarvol.skyvibe.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.yarvol.skyvibe.location.LocationCallbacks
import com.yarvol.skyvibe.location.LocationService
import com.yarvol.skyvibe.network.CurrentTemperature
import com.yarvol.skyvibe.network.WeatherApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckCurrentTemperatureInCurrentLocation(
    locationService: LocationService,
    weatherApi: WeatherApi,
    callbacks: LocationCallbacks
) {
    val context = LocalContext.current

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    var isLoading by remember { mutableStateOf(false) }
    var currentTemperatureData by remember { mutableStateOf<CurrentTemperature?>(null) }

    val scope = rememberCoroutineScope()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.size(width = 248.dp, height = 68.dp),
            onClick = {
                isLoading = true
                if (permissionState.allPermissionsGranted) {
                    if (locationService.areLocationPermissionsGranted(context)) {
                        locationService.getCurrentLocation(
                            onSuccess = {
                                callbacks.onSuccess(it)
                                scope.launch {
                                    try {
                                        currentTemperatureData = weatherApi.getCurrentTemperature(it.first, it.second)
                                        errorMessage = null
                                    } catch (e: Exception) {
                                        errorMessage =
                                            "Не удалось получить температуру: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            onFailure = {
                                errorMessage = "Не удалось получить местоположение"
                                callbacks.onFailure
                            }
                        )
                    } else {
                        callbacks.onPermissionDenied
                        errorMessage = "Разрешения отклонены"
                    }
                } else {
                    permissionState.launchMultiplePermissionRequest()
                }
            }) {
            Text(
                textAlign = TextAlign.Center,
                text = "Получить текущую температуру по локации"
            )
        }

        LaunchedEffect(key1 = permissionState) {
            val allPermissionsRevoked =
                permissionState.permissions.size == permissionState.revokedPermissions.size

            val permissionsToRequest = permissionState.permissions.filter {
                !it.status.isGranted
            }

            if (permissionsToRequest.isNotEmpty()) permissionState.launchMultiplePermissionRequest()

            if (allPermissionsRevoked) {
                callbacks.onPermissionsRevoked()
            } else {
                if (permissionState.allPermissionsGranted) {
                    callbacks.onPermissionGranted()
                } else {
                    callbacks.onPermissionDenied()
                }
            }
        }

        Spacer(Modifier.padding(16.dp))

        if (errorMessage != null) {
            Text(
                text = "Ошибка: $errorMessage",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
        } else
        if (currentTemperatureData != null) {
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
}