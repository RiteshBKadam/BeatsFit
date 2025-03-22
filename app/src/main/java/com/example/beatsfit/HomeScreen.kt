package com.example.beatsfit

import BottomAppBarWithIcons
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locationapp.LocationUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

const val REQUEST_OAUTH_REQUEST_CODE = 1
val fitnessOptions = buildFitnessOptions()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    context: Context,
    account: GoogleSignInAccount,
    navController: NavController
) {
    var isPermissionGranted by remember { mutableStateOf(false) }
    if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
        // Check if permissions are already granted
        val activityRecognitionPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (activityRecognitionPermission != PackageManager.PERMISSION_GRANTED ||
            fineLocationPermission != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_OAUTH_REQUEST_CODE
            )
        } else {
          isPermissionGranted=true
        }
    } else {
        GoogleSignIn.requestPermissions(
            context as Activity,
            REQUEST_OAUTH_REQUEST_CODE,
            account,
            fitnessOptions
        )
    }


    if (isPermissionGranted) {
        data(context, account, navController)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text("Waiting for permissions...", color = Color.White)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun data(context: Context, account: GoogleSignInAccount, navController: NavController) {

    val beatsfitViewModel:BeatsfitViewModel = viewModel()

    val locationUtils = LocationUtils(context)
    val locationViewModel: LocationViewModel = viewModel()

    val scrollState= rememberScrollState()

    val healthData by beatsfitViewModel.healthData.collectAsState()
    val isPermissionGranted by beatsfitViewModel.isPermissionGranted.collectAsState()


    LaunchedEffect (Unit){
        beatsfitViewModel.startFetchingHealthData(
            context, account,
            locationUtils,
            locationViewModel
        )
    }


    Scaffold(
        bottomBar = { BottomAppBarWithIcons(navController) },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp)
                    .verticalScroll(scrollState)
                    .background(Color(0xFF0f191f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier.padding(top = 85.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .size(155.dp)
                            .background(color = Color(0xD3FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("YOU'RE", color = Color(0xFF02092F), fontSize = 26.sp)
                            Text("SAFE!", color = Color(0xFF02092F), fontSize = 27.sp)
                        }
                    }
                    Row(
                        Modifier.width(250.dp).padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {


                        Column(verticalArrangement = Arrangement.spacedBy(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text="${healthData.heartRate}", fontSize = 18.sp)

                            Image(
                                painter = painterResource(R.drawable.heartbeat),
                                contentDescription = "steps",
                                modifier = Modifier.size(23.dp)
                            )

                        }


                        Column(verticalArrangement = Arrangement.spacedBy(5.dp), horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(start = 10.dp)
                        ) {
                            Text(text="${healthData.steps}", fontSize = 18.sp)
                            Image(
                                painter = painterResource(R.drawable.steps),
                                contentDescription = "steps",
                                modifier = Modifier.size(25.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${((healthData.distance * 100).toInt() / 10000f)}", fontSize = 18.sp)

                        Image(
                            painter = painterResource(R.drawable.km),
                            contentDescription = "steps",
                            modifier = Modifier.size(25.dp)
                        )
                    }


                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .fillMaxWidth()
                                .clickable { navController.navigate("friends") }
                                .wrapContentHeight()
                                .background(color = Color(0x1BFFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Tip of the Day",
                                    color = Color(0xFFda9d5f),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    // Display health tips based on user's activity
                                    val healthTip = when {
                                        // Add conditions based on user's activity or health data (e.g., {healthData.steps}, heart rate)
                                        healthData.steps < 5000 -> "Time for a walk! Your steps goal is just around the corner."
                                        healthData.heartRate > 100 -> "Relax and breathe deeply. Your heart rate seems a bit high."
                                        healthData.calories < 300 -> "Keep moving! Try to burn a few more calories today."
                                        else -> "Great job today! Keep up the good work and stay active!"
                                    }

                                    Text(
                                        text = healthTip,
                                        fontSize = 16.sp,
                                        style = TextStyle(lineHeight = 18.sp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.padding(20.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .fillMaxWidth()
                                .clickable { navController.navigate("friends") }
                                .wrapContentHeight()
                                .background(color = Color(0x1BFFFFFF)),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Achieve Your Goals!",
                                        color = Color(0xFFda9d5f),
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "Set and track your daily health goals to stay active and healthy. Keep smashing those milestones!",
                                        fontSize = 14.sp,
                                        style = TextStyle(lineHeight = 16.5.sp),
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = 0.7f, // Example progress (70%)
                                        color = Color(0xFFda9d5f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                    Text(
                                        text = "Steps: 7000 / 10000",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Image(
                                    painter = painterResource(id = R.drawable.vitals), // Replace with your asset
                                    contentDescription = "Daily Goals",
                                    Modifier.size(80.dp)
                                )
                            }
                        }
                        Spacer(Modifier.padding(20.dp))


                        Box(
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .fillMaxWidth()
                                .clickable {navController.navigate("friends") }
                                .wrapContentHeight()
                                .background(color = Color(0x1BFFFFFF)),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly) {
                                Column(modifier = Modifier.width(230.dp)){
                                    Box {
                                        Text(
                                            text = "Let your family care you!",
                                            modifier = Modifier
                                                .align(alignment = Alignment.TopStart)
                                                .padding(8.dp),
                                            color = Color(0xFFda9d5f),
                                            fontSize = 20.sp,
                                        )
                                    }
                                    Text(
                                        text = "Stay connected with loved ones as they monitor your health in real time, ensuring care and support when you need it most.",
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(8.dp, 0.dp, 8.dp, 20.dp),
                                        textAlign = TextAlign.Justify,
                                        style = TextStyle(lineHeight = 16.5.sp)
                                    )
                                }
                                Image(painter = painterResource(id = R.drawable.family), contentDescription = "family",Modifier.size(110.dp))
                            }
                        }
                        Spacer(Modifier.padding(20.dp))

                        Box(
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .fillMaxWidth()
                                .clickable { navController.navigate("health") }
                                .wrapContentHeight()
                                .background(color = Color(0x1BFFFFFF)),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f) // Use weight to take up the remaining space
                                        .padding(start = 8.dp)
                                ) {
                                    Box {
                                        Text(
                                            text = "Track your Health!",
                                            modifier = Modifier
                                                .align(alignment = Alignment.TopStart)
                                                .padding(0.dp, 8.dp, 0.dp, 8.dp),
                                            color = Color(0xFFda9d5f),
                                            fontSize = 20.sp,
                                        )
                                    }
                                    Text(
                                        text = "Easily track your vital health metrics\nall in one place to stay on top of your\nwell-being.",
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(0.dp, 0.dp, 8.dp, 20.dp),
                                        textAlign = TextAlign.Start, // Ensures text aligns left
                                        style = TextStyle(lineHeight = 16.5.sp)
                                    )
                                }
                                Image(
                                    painter = painterResource(id = R.drawable.vitals),
                                    contentDescription = "family",
                                    Modifier
                                        .size(80.dp)
                                        .weight(0.5f) // Use weight to take up the remaining space

                                )
                            }
                        }

                        Button(
                            onClick = { logout(navController, context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text(text = "Logout")
                        }
                        Button(
                            onClick = { navController.navigate("location") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text(text = "Logout")
                        }
                    }

                }
            }
        }
    )
}


fun logout(navController: NavController, context: Context) {
    val googleSignInClient =
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    googleSignInClient.revokeAccess()
    googleSignInClient.signOut()
        .addOnCompleteListener {
            // Navigate back to login screen after sign out
            navController.navigate("initiator") {
                popUpTo("home_screen") { inclusive = true }
            }
        }
}

fun buildFitnessOptions(): FitnessOptions {
    return FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_LOCATION_SAMPLE, FitnessOptions.ACCESS_READ)
        .build()
}
