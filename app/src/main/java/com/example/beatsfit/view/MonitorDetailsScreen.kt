package com.example.beatsfit.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.beatsfit.R
import com.example.beatsfit.util.FetchFriendsDetails
import com.example.beatsfit.util.getIconForMetric
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorDetailsScreen(context: Context, friendsId: String,navController: NavHostController) {
    val healthData= FetchFriendsDetails(friendsId)

        Scaffold(
            bottomBar = { BottomAppBarWithIcons(navController = navController) },
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("Health Details", color = Color.White) },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(
                            0xFF0f191f
                        )
                    )
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
                        if(healthData.second.isNotEmpty()){
                        healthData.first.forEach { collectionName ->
                            val docs=healthData.second[collectionName]
                            if (!docs.isNullOrEmpty()) {
                                val latestValue = docs.lastOrNull()?.get("value")?.toString() ?: "N/A"
                                    HealthMetricCard(
                                        title = collectionName.replaceFirstChar { it.uppercaseChar() },
                                        value = latestValue,
                                        iconRes =getIconForMetric(collectionName),
                                        navController = navController
                                    )
                                }
                            }
                    }
                    }
                }
            }
        )
}