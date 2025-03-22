package com.example.beatsfit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationapp.LocationUtils
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BeatsfitViewModel : ViewModel() {
    private val _healthData = MutableStateFlow(
        HealthData(
            steps = 0f,
            distance = 0f,
            heartRate = 0f,
            calories = 0f,
            latitude = 0.0,
            longitude = 0.0
        )
    )
    val healthData: StateFlow<HealthData> = _healthData

    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted

    fun updatePermissionStatus(granted: Boolean) {
        _isPermissionGranted.value = granted
    }

    fun fetchHealthData(
        context: Context,
        account: GoogleSignInAccount,
        locationUtils: LocationUtils,
        locationViewModel: LocationViewModel
    ) {
        viewModelScope.launch {
            val location = locationViewModel.location.value
            delay(1000)
            fetchGoogleFitData(
                context = context,
                account = account,
                onDataReceived = { steps, distance, heartRate, calories, latitude, longitude ->
                    _healthData.value = HealthData(
                        steps = steps,
                        distance = distance,
                        heartRate = heartRate,
                        calories = calories,
                        latitude = location?.latitude ?: 0.0,
                        longitude = location?.longitude ?: 0.0
                    )
                },
                locationUtils = locationUtils,
                viewModel = locationViewModel
            )
        }
    }

    fun startFetchingHealthData(
        context: Context,
        account: GoogleSignInAccount,
        locationUtils: LocationUtils,
        locationViewModel: LocationViewModel
    ) {
        viewModelScope.launch {
            while (true) {
                fetchHealthData(context, account, locationUtils, locationViewModel)
                delay(1500)
            }
        }
    }
}
