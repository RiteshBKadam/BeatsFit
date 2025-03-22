package com.example.beatsfit

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun SETTINGS(navController: NavController,context: Context) {
    logout(navController = navController, context = context)
}