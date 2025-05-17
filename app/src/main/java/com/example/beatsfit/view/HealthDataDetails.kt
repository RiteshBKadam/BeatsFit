package com.example.beatsfit.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.beatsfit.R
import com.example.beatsfit.model.HealthData
import com.example.beatsfit.util.BottomAppBarWithIcons
import com.example.beatsfit.viewmodel.BeatsfitViewModel
import com.example.beatsfit.viewmodel.LocationViewModel
import com.example.beatsfit.util.LocationUtils
import com.example.beatsfit.util.TopAppBar
import com.example.beatsfit.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun HealthDetailScreen(
    context: Context,
    userViewModel: UserViewModel,
    account: GoogleSignInAccount,
    navController: NavHostController,
    beatsfitViewModel: BeatsfitViewModel,
    locationViewModel: LocationViewModel,
    locationUtils: LocationUtils
) {

    val healthData by beatsfitViewModel.healthData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        beatsfitViewModel.startFetchingHealthData(
            context = context,
            account = account,
            locationUtils = locationUtils,
            locationViewModel = locationViewModel
        )
    }
    HealthDetails(navController,healthData,userViewModel,context)


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDetails(
    navController: NavController,
    healthData: HealthData,
    userViewModel: UserViewModel,
    context: Context
) {
    val scrollState= rememberScrollState()
    var showSleep by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomAppBarWithIcons(navController = navController) },
        topBar = { TopAppBar(navController, userViewModel, context ) },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF0f191f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Your Health Metrics",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 25.dp)
                    )

                    HealthMetricCard("Steps", "${healthData.steps.toInt()} steps", R.drawable.steps, navController)
                    HealthMetricCard("Calories Burned", "${healthData.calories} kcal", R.drawable.calories, navController)
                    HealthMetricCard("Heart Rate", "${healthData.heartRate} bpm", R.drawable.heartbeat, navController)
                    HealthMetricCard(
                        "Distance",
                        "${String.format("%.2f", healthData.distance/1000)} km",
                        R.drawable.km,
                        navController
                    )

                    HealthMetricCard("Sleep", "${healthData.sleepData["totalSleep"]} ", R.drawable.sleep, navController)

                    HealthMetricCard(
                        "Location",
                        "${healthData.latitude} \n ${healthData.longitude}",
                        R.drawable.baseline_location_pin_24,
                        navController
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    )
}

@Composable
fun HealthMetricCard(title: String, value: String, iconRes: Int, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("graphDetail/$title") }
            .height(80.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x1BFFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconRes),
                    alpha = 0.91f,
                    contentDescription = title,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    color = Color(0xE2FFFFFF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = value,
                color = Color(0xFFda9d5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
