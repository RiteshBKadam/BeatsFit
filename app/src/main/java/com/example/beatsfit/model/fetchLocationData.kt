package com.example.beatsfit.model

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.beatsfit.view.MainActivity
import com.example.beatsfit.viewmodel.LocationViewModel
import com.example.beatsfit.util.LocationUtils


@Composable
fun LocationTrackerScreen(
    locationUtils: LocationUtils,
    context: Context,
    viewModel: LocationViewModel,
    navController: NavHostController
) {
    val location = viewModel.location.value
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationalRequired) {
                    Toast.makeText(
                        context,
                        "Location Permission Granted...!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, "Enable the location in settings", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (location != null) {
            val uri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}")

            // Create an Intent to launch Google Maps
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)
        } else {
            Text(text = "Location not available")
        }

        // Button to request location permissions and updates
        Button(onClick = {
            if (locationUtils.hasLocationPermission(context)) {
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Grant Location Here")
        }
    }
}
