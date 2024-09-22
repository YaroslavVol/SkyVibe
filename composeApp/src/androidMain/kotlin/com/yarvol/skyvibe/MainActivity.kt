package com.yarvol.skyvibe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yarvol.skyvibe.location.LocationCallbacks
import com.yarvol.skyvibe.location.LocationService
import com.yarvol.skyvibe.network.WeatherApi
import com.yarvol.skyvibe.ui.CitySelection
import com.yarvol.skyvibe.ui.CheckCurrentTemperatureInCurrentLocation

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val locationService = LocationService(fusedLocationProviderClient)

        setContent {

            var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            val callbacks = LocationCallbacks(
                onSuccess = { coordinates -> location = coordinates },
                onFailure = { exception -> errorMessage = "Не удалось получить местоположение: ${exception.message}" },
                onPermissionGranted = { errorMessage = null },
                onPermissionDenied = { errorMessage = "Разрешения отклонены" },
                onPermissionsRevoked = { errorMessage = "Все разрешения отозваны" }
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val client = remember { WeatherApi() }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CheckCurrentTemperatureInCurrentLocation(
                        weatherApi = client,
                        locationService = locationService,
                        callbacks = callbacks
                    )
                    Spacer(Modifier.padding(32.dp))
                    CitySelection()
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

