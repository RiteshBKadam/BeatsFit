package com.example.beatsfit.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.example.beatsfit.R
import com.example.beatsfit.util.BottomAppBarWithIcons
import com.example.beatsfit.util.TopAppBar
import com.example.beatsfit.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.util.DataUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphDetailScreen(title: String,
                      navController: NavController,
                      context: Context,
                      userViewModel: UserViewModel,
                      account: GoogleSignInAccount) {
    Scaffold(
        topBar = {
            TopAppBar(navController, userViewModel, context )
        },
        bottomBar = { BottomAppBarWithIcons(navController) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF0f191f))
            ) {
                var resource by remember { mutableIntStateOf(R.drawable.goals) }
                var borderColor by remember { mutableStateOf(Color.White) }
                when(title){
                    "Steps" -> {resource = R.drawable.steps
                        borderColor=Color(0xFF3997FD)
                    }
                    "Heart Rate" -> {resource = R.drawable.heartbeat
                        borderColor=Color(0xFFE66A6F)
                    }
                    "Calories Burned" -> {resource = R.drawable.calories
                        borderColor=Color(0xFFDD8043)
                    }
                    "Distance" -> {resource = R.drawable.km
                        borderColor=Color(0xFF79D153)
                    }
                    "Sleep" -> {resource = R.drawable.sleep_purple
                        borderColor=Color(0xFF663AB6)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                BeatsFitBarChart()

            }
        }
    )
}



@Composable
fun BeatsFitBarChart() {
    val barData = listOf(
        BarData(Point(0f, 8000f), label = "Steps"),
        BarData(Point(1f, 450f), label = "Calories"),
        BarData(Point(2f, 7.5f), label = "Sleep"),
        BarData(Point(3f, 72f), label = "Heart Rate")
    )

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(barData.size - 1)
        .labelData { index -> barData[index].label }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { i ->
            val maxY = barData.maxOf { it.point.y }
            val step = maxY / 5
            (i * step).toInt().toString()
        }
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            barWidth = 20.dp)
    )

    BarChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        barChartData = barChartData
    )
}

