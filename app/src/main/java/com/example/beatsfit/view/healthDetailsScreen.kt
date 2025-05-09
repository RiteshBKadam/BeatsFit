package com.example.beatsfit.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.beatsfit.R
import com.example.beatsfit.util.BottomAppBarWithIcons

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HealthDetailsScreen(navController:NavController) {
    Scaffold(
        bottomBar = { BottomAppBarWithIcons(navController = navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            // Cards
            val cardData = listOf(
                CardData("Steps", 100, "1000 steps", R.drawable.steps, Brush.linearGradient(colors = listOf(Color.Blue, Color.Cyan))),
                CardData("Sleep", 20, "6 Hours", R.drawable.steps, Brush.linearGradient(colors = listOf(Color.Blue, Color.Blue))),
                CardData("Heart", 90, "90 bpm", R.drawable.steps, Brush.linearGradient(colors = listOf(Color.Red, Color.Magenta))),
                CardData("Calories Burned", 30, "300 kcal", R.drawable.steps, Brush.linearGradient(colors = listOf(Color.Blue, Color.Red))),
                CardData("Distance", 50, "15 km", R.drawable.steps, Brush.linearGradient(colors = listOf(Color.Green, Color.Blue))),
                CardData("Location", null, "Shivajinagar, JM Road", R.drawable.steps, null)
            )

            val rows = cardData.chunked(2)
            for (row in rows) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    row.forEach { CardItem(data = it) }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CardItem(data: CardData) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (data.progress != null) {
                CircularProgressBar(progress = data.progress, brush = data.gradient!!)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = data.icon), // Replace with your icon resource
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White, fontWeight = FontWeight.Bold
                )
                Text(
                    text = data.value,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CircularProgressBar(progress: Int, brush: Brush) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer {
                clip = true
                shape = CircleShape
                shadowElevation = 4.dp.toPx()
            }
    ) {
        CircularProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 8.dp,
            color = Color.Transparent,
        )
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )
    }
}

data class CardData(
    val title: String,
    val progress: Int?,
    val value: String,
    val icon: Int,
    val gradient: Brush?
)