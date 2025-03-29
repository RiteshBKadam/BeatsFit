package com.example.beatsfit.util

import okhttp3.Route

sealed class Screens(val route: String){
    object Home: Screens("home_screen")
    object Health_Details: Screens("health")
    object Members: Screens("members")
    object Settings: Screens("settings_screen")
}
