package com.example.beatsfit.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.hasPermissions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions

private const val REQUEST_OAUTH_REQUEST_CODE=1

@Composable
fun PermissionScreen(account: GoogleSignInAccount,
                     context: Context,
                     navController: NavHostController
){
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
    val bat = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    )
    val bg = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND
    )

    if (activityRecognitionPermission != PackageManager.PERMISSION_GRANTED ||
        fineLocationPermission != PackageManager.PERMISSION_GRANTED ||
        loca!= PackageManager.PERMISSION_GRANTED
//        ||
//        fitnessOptions?.let { hasPermissions(account, it) } == false
//        bat!= PackageManager.PERMISSION_GRANTED||
//        bg!= PackageManager.PERMISSION_GRANTED
    ) {
        Column {
            Button(onClick = {askForPermissions(context,account)}) {
                Text("Waiting for permissions...")
            }
        }

    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Text("THISSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS")
        isPermissionGranted=true
        navController.navigate("home_screen/$isPermissionGranted")
            }
    }
}

fun askForPermissions(context: Context,account: GoogleSignInAccount) {
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        REQUEST_OAUTH_REQUEST_CODE
    )

    }
