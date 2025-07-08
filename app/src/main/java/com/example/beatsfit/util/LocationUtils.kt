package com.example.beatsfit.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.beatsfit.viewmodel.LocationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.firestore

class LocationUtils(val context: Context) {

    private val _fusedLocationClient: FusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewModel: LocationViewModel) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location = LocationData(latitude = it.latitude, longitude = it.longitude)
                    viewModel.updateLocation(location)

                }
            }
        }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        _fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    @SuppressLint("MissingPermission")
    fun liveLocationUpdate() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val account= GoogleSignIn.getLastSignedInAccount(context)
                    val location = LocationData(latitude = it.latitude, longitude = it.longitude)
                    val userId = account?.id.toString()

                    if (userId != null) {
                        val locationMap = hashMapOf(
                            "latitude" to it.latitude,
                            "longitude" to it.longitude,
                            "timestamp" to System.currentTimeMillis()
                        )

                        com.google.firebase.Firebase.firestore
                            .collection("userLiveLocation")
                            .document(userId)
                            .set(locationMap)
                    }
                }
            }
        }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        _fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }




    fun hasLocationPermission(context: Context):Boolean{
        return ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
    }
}