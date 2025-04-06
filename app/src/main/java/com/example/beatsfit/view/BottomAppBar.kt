package com.example.beatsfit.view

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.beatsfit.R

@Composable
fun BottomAppBarWithIcons(navController: NavController) {
   BottomAppBar(
        containerColor = Color(0xFF122931),
        contentColor = Color.White,
        modifier = Modifier.height(64.dp),
        actions = {
            IconButton(
                onClick = {
                    val currentRoute =
                        navController.currentBackStackEntry?.destination?.route
                    if (currentRoute != "home_screen") {
                        navController.navigate("home_screen"){
                            popUpTo("initiator")
                        }
                    }
                },
                modifier = Modifier.weight(0.26f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_home),
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(
                onClick = {
                    // Get the current route
                    val currentRoute =
                        navController.currentBackStackEntry?.destination?.route
                    // Navigate only if the current route is not already "health"
                    if (currentRoute != "health") {
                        navController.navigate("health"){
                            popUpTo("initiator")
                        }
                    }
                },
                modifier = Modifier.weight(0.26f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            IconButton(
                onClick = {
                    val currentRoute =
                        navController.currentBackStackEntry?.destination?.route
                    // Navigate only if the current route is not already "profile_screen"
                    if (currentRoute != "trackFamily") {
                        navController.navigate("trackFamily"){
                            popUpTo("initiator")
                        }
                    }
                },
                modifier = Modifier.weight(0.26f)

            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "MonitorFamily",
                    tint= Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }

            IconButton(
                onClick = {
                    // Get the current route
                    val currentRoute =
                        navController.currentBackStackEntry?.destination?.route
                    // Navigate only if the current route is not already "profile_screen"
                    if (currentRoute != "friends") {
                        navController.navigate("members"){
                            popUpTo("initiator")
                        }
                    }
                },
                modifier = Modifier.weight(0.26f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.group_family),
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }


            IconButton(
                onClick = {
                    // Get the current route
                    val currentRoute =
                        navController.currentBackStackEntry?.destination?.route
                    // Navigate only if the current route is not already "settings_screen"
                    if (currentRoute != "user_profile") {
                        navController.navigate("user_profile"){
                            popUpTo("initiator")
                        }
                    }
                },
                modifier = Modifier.weight(0.25f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    )
}
