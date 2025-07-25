package com.riteshbkadam.beatsfitapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.riteshbkadam.beatsfitapp.R
import kotlinx.coroutines.delay


@Composable
fun Splash(navController: NavController, isLoggedIn: Boolean) {
    val context = LocalContext.current
    val mistrully = FontFamily(
        Font(R.font.mistrully, FontWeight.Normal),
    )
    val quicksand= FontFamily(
        Font(R.font.quicksand, FontWeight.Normal)
    )
    LaunchedEffect(Unit) {
        delay(2000)

        if (isLoggedIn) {
            navController.navigate("home_screen") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("initiator") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFF0f191f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {

        Row {
            Text("Beats", fontFamily = quicksand, fontSize = 50.sp, color = Color.White)
            Text("Fit", fontFamily = mistrully, fontSize = 48.sp, color = Color.White)

        }
    }
}
