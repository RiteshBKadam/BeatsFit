package com.example.beatsfit.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.beatsfit.util.greetString
import java.util.Calendar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var title by remember { mutableStateOf("") }
    var alpha by remember { mutableFloatStateOf(0.0F) }

    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            "home_screen" -> {
                if (greetString() != "Warm Late Night") {
                    title = "Good ${greetString()}"
                    alpha = 1.0F
                } else {
                    title = ""
                    alpha = 0.0F
                }
            }
            "members" -> { title = "Members"; alpha = 0.0F }
            "health" -> { title = "Health Details"; alpha = 0.0F }
            "user_profile" -> { title = "Profile"; alpha = 0.0F }
        }
    }

    Column {
        androidx.compose.material3.TopAppBar(
            title = { AppBarTitle(title, alpha) }, // Extracted to avoid recomposition
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color(0xFF0f191f)
            )
        )
    }
}

@Composable
fun AppBarTitle(title: String, alpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, end = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Text(
                title,
                color = Color.White,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            AnimatedAlphaText(alpha) // Extracted composable to handle alpha updates
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    }
}

@Composable
fun AnimatedAlphaText(alpha: Float) {
    Text(
        "Aryan",
        color = Color(0xFFE3A356),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.alpha(alpha)
    )
}

