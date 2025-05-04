package com.example.beatsfit.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.beatsfit.util.isUserLoggedIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.delay

private const val REQUEST_OAUTH_REQUEST_CODE=1

@Composable
fun PermissionScreen(
                     context: Context,
                     navController: NavHostController
) {
    var isPermissionGranted by remember { mutableStateOf(false) }

    val activityRecognitionPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACTIVITY_RECOGNITION
    )
    val fineLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val loca = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (activityRecognitionPermission != PackageManager.PERMISSION_GRANTED ||
        fineLocationPermission != PackageManager.PERMISSION_GRANTED ||
        loca != PackageManager.PERMISSION_GRANTED
    ) {
        Column {
            Button(onClick = {
                isPermissionGranted = askForPermissions(
                    context,
                    activityRecognitionPermission,
                    fineLocationPermission,
                    loca
                )
                if (isPermissionGranted) {
                    Toast.makeText(context, isPermissionGranted.toString(), Toast.LENGTH_LONG)
                        .show()
                    navController.navigate("home_screen/$isPermissionGranted")

                }
            })
            {
                Text("Waiting for permissions...")
            }
        }

    } else {

        LaunchedEffect(Unit) {
            Toast.makeText(context, isUserLoggedIn(context).toString() + "this", Toast.LENGTH_LONG)
                .show()
            if (isUserLoggedIn(context)) {
                val isPermissionGranted = true
                navController.navigate("home_screen/$isPermissionGranted")
                delay(1000)
            } else {
                navController.navigate("initiator")
            }
        }
    }
}


fun askForPermissions(
    context: Context,
    activityRecognitionPermission: Int,
    fineLocationPermission: Int,
    loca: Int
): Boolean {
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        REQUEST_OAUTH_REQUEST_CODE
    )
    if (activityRecognitionPermission == PackageManager.PERMISSION_GRANTED ||
        fineLocationPermission == PackageManager.PERMISSION_GRANTED ||
        loca!= PackageManager.PERMISSION_GRANTED){
        return true
    }else{
        return false
    }
    }
