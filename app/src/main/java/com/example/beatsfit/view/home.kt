package com.example.beatsfit.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.beatsfit.viewmodel.BeatsfitViewModel
import com.example.beatsfit.model.HealthData
import com.example.beatsfit.util.LocationUtils
import com.example.beatsfit.viewmodel.LocationViewModel
import com.example.beatsfit.R
import com.example.beatsfit.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

private const val REQUEST_OAUTH_REQUEST_CODE=1
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FitnessScreen(navController: NavController, context: Context, account: GoogleSignInAccount,beatsfitViewModel: BeatsfitViewModel) {
    val fitnessOptions = buildFitnessOptions()

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
        val loca = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val bat = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val bg = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (activityRecognitionPermission != PackageManager.PERMISSION_GRANTED ||
            fineLocationPermission != PackageManager.PERMISSION_GRANTED ||
            loca!=PackageManager.PERMISSION_GRANTED||
            bat!=PackageManager.PERMISSION_GRANTED||
            bg!=PackageManager.PERMISSION_GRANTED
        ) {
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
        } else {
            isPermissionGranted = true
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

        homeScreen(
            context, account, navController,  beatsfitViewModel,

        )

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

@Composable
fun homeScreen(context: Context,account: GoogleSignInAccount,navController:NavController, beatsfitViewModel: BeatsfitViewModel) {

    var isExpanded by remember { mutableStateOf(false) }

    val alphaVal by animateFloatAsState(
        targetValue = if (isExpanded) 0.6f else 1f,
        animationSpec = tween(durationMillis = 0)
    )
    val locationUtils = LocationUtils(context)
    val locationViewModel: LocationViewModel = viewModel()

    val scrollState = rememberScrollState()

    val healthData by beatsfitViewModel.healthData.collectAsState()


    // Animate position changes of the FAB using animateDpAsState
    val offsetX by animateDpAsState(
        targetValue = if (isExpanded) (-70).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = ""
    )

    val offsetY1 by animateDpAsState(
        targetValue = if (isExpanded) (-30).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = ""
    )

    val offsetX2 by animateDpAsState(
        targetValue = if (isExpanded) (0).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = ""
    )

    val offsetY2 by animateDpAsState(
        targetValue = if (isExpanded) (-73).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = ""
    )

    LaunchedEffect(Unit) {
        beatsfitViewModel.startFetchingHealthData(
            context, account,
            locationUtils,
            locationViewModel
        )
    }


        Scaffold(
            modifier = Modifier.background(Color(0xFF0f191f)),
            topBar = {
                TopAppBar(navController)
            },
            bottomBar = { BottomAppBarWithIcons(navController) },
            floatingActionButton = {
                Box {

                    FloatingActionButton(
                        onClick = { isExpanded = !isExpanded },
                        containerColor = Color(0xFFFF0000),
                        shape = CircleShape,
                        modifier = Modifier
                            .offset(x = offsetX, y = offsetY1)
                            .border(1.dp, Color.Red, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.call),
                            contentDescription = "Share"
                        )
                    }
                    FloatingActionButton(
                        onClick = { isExpanded = !isExpanded },
                        containerColor = Color(0xFFFF0000),
                        shape = CircleShape,
                        modifier = Modifier
                            .offset(x = offsetX2, y = offsetY2)
                            .border(1.dp, Color.Red, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_notifications_active_24),
                            contentDescription = "Edit"
                        )
                    }
                    Toast.makeText(context, account.photoUrl?.path ?: "nll", Toast.LENGTH_SHORT).show()

                    FloatingActionButton(
                        onClick = { isExpanded = !isExpanded },
                        containerColor = Color(0xFFFF0000),
                        shape = CircleShape,
                        modifier = Modifier.border(1.dp, Color.Red, CircleShape)

                    ) {
                        Text("SOS")
                    }
                }
            },
            content = {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .verticalScroll(scrollState)
                        .fillMaxSize()
                ) {

                    FitnessContent(healthData, alphaVal)
                }
            }
        )
}
@Composable
fun FitnessContent(healthData: HealthData, alphaVal:Float) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alphaVal)
            .background(Color(0xFF0f191f))
            .padding(15.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value =searchText,
            onValueChange = {searchText=it},
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            textStyle =TextStyle(fontSize =18.sp, ),
            placeholder = { Text("Search...", fontSize = 18.sp) },
            shape = RoundedCornerShape(24.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            }
        )


        Spacer(modifier = Modifier.height(24.dp))

        // Tip of the Day card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732)),
            border = BorderStroke(1.dp, Color(0x50E3A356))
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                text = "Tip of the Day",
                color = Color(0xFFda9d5f),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            val healthTip = when {
                healthData.steps < 5000 -> "Time for a walk! Your steps goal is just around the corner."
                healthData.heartRate > 100 -> "Relax and breathe deeply. Your heart rate seems a bit high."
                healthData.calories < 300 -> "Keep moving! Try to burn a few more calories today."
                else -> "Great job today! Keep up the good work and stay active!"
            }

            Text(
                modifier = Modifier.padding(15.dp),
                text = healthTip,
                fontSize = 17.sp,
                style = TextStyle(lineHeight = 18.sp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(
                value = healthData.heartRate.toString(),
                unit = "BPM",
                color = Color(0xFFFF6B6B),
                icon = Icons.Default.Favorite
            )
            StatItem(
                value = healthData.steps.toString(),
                unit = "Steps",
                color = Color(0xFF4DABF7),
                icon = Icons.Default.Person
            )
            StatItem(
                value = healthData.distance.toString(),
                unit = "km",
                color = Color(0xFF51CF66),
                icon = Icons.Default.LocationOn
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732)),
            border = BorderStroke(1.dp, Color(0x50E3A356))
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
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
        Spacer(Modifier.padding(15.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732)),
            border = BorderStroke(1.dp, Color(0x50E3A356))
        ){
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
        Spacer(Modifier.padding(15.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732)),
            border = BorderStroke(1.dp, Color(0x50E3A356))
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
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

    }
}

@Composable
fun StatItem(
    value: String,
    unit: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            unit,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}
fun logout(navController: NavController, context: Context,userViewModel: UserViewModel) {
    val googleSignInClient =
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    googleSignInClient.revokeAccess()
    userViewModel.clearDatabase()
    googleSignInClient.signOut()
        .addOnCompleteListener {

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
