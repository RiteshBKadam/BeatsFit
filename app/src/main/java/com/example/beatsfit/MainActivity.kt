package com.example.beatsfit

import FitnessScreen
import GraphDetailScreen
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beatsfit.ui.theme.BeatsFitTheme
import com.example.locationapp.LocationUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

private const val UNIQUE_WORK_NAME = "health_data_sync"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BeatsFitTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setStatusBarColor(
                    color = Color(0XFF0f191f),
                    darkIcons = false
                )
                AppNavGraph(context = LocalContext.current)
            }
        }

    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavGraph(context: Context) {
    val navController = rememberNavController()
    val beatsfitViewModel:BeatsfitViewModel = viewModel()
    val locationUtils = LocationUtils(context)
    val locationViewModel: LocationViewModel = viewModel()


    NavHost(navController, startDestination = "initiator") {

        composable("initiator") {
            val googleSignInClient = createGoogleSignInClient(context)
            Initiator(
                context = context,
                googleSignInClient = googleSignInClient,
                navController = navController
            )
        }

        composable("home_screen") {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            account?.let {
//                FitnessScreen(navController,context)
                FitnessScreen(
                    context = context, account = it,
                    navController = navController,
                )
            }
        }

        composable("new_user") {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            account?.let {
                NewUser(
                    context = context, account = it, navController = navController
                )
            }
        }

        composable("location") {
            LocationTrackerScreen(
                context = context,
                navController = navController,
                locationUtils = LocationUtils(context),
                viewModel = locationViewModel
            )
        }
        composable("friends") {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                FriendsScreen(navController,account)
            }

        }
        composable("health") {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
//                HealthDetailsScreen(navController)
                HealthDetailScreen(context, account =account,navController, beatsfitViewModel, locationViewModel  , locationUtils)
            }
        }
        composable(
            route = "graphDetail",
        ) {
            GraphDetailScreen(title = "title", chartData = listOf(7800f, 8200f, 7600f, 8500f, 8900f, 9100f, 8700f))
        }

        composable("members") {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                Members( account =account,navController=navController, context = context)
            }
        }
        composable("settings_screen") {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                SETTINGS(navController,context)
            }
        }

    }
}
fun createGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}