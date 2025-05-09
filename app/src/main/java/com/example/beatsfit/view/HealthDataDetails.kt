package com.example.beatsfit.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.beatsfit.R
import com.example.beatsfit.util.BottomAppBarWithIcons
import com.example.beatsfit.viewmodel.BeatsfitViewModel
import com.example.beatsfit.viewmodel.LocationViewModel
import com.example.beatsfit.util.LocationUtils
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDetailScreen(
    context: Context,
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

    Scaffold(
        bottomBar = { BottomAppBarWithIcons(navController = navController) },
        topBar = {
            TopAppBar(
                title = { Text("Health Details", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF0f191f))
            )
        },
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Your Health Metrics",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    HealthMetricCard("Steps", "${healthData.steps.toInt()} steps", R.drawable.steps, navController)
                    HealthMetricCard("Heart Rate", "${healthData.heartRate} bpm", R.drawable.heartbeat, navController)
                    HealthMetricCard("Calories Burned", "${healthData.calories} kcal", R.drawable.vitals, navController)
                    HealthMetricCard(
                        "Distance",
                        "${String.format("%.2f", healthData.distance/1000)} km",
                        R.drawable.km,
                        navController
                    )
                    HealthMetricCard(
                        "Location",
                        "${healthData.latitude} \n ${healthData.longitude}",
                        R.drawable.location_vector,
                        navController
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Add functionality here */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFda9d5f))
                    ) {
                        Text("View More Details", color = Color.White)
                    }
                }
            }
        }
    )
}

@Composable
fun HealthMetricCard(title: String, value: String, iconRes: Int, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .clickable { navController.navigate("graphDetail") }
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
                    contentDescription = title,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    color = Color.White,
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
