package com.example.beatsfit.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.beatsfit.room.data.UserRepository
import com.example.beatsfit.ui.theme.BeatsFitTheme
import com.example.beatsfit.viewmodel.BeatsfitViewModel
import com.example.beatsfit.viewmodel.LocationViewModel
import com.example.beatsfit.util.LocationUtils
import com.example.beatsfit.util.UserViewModelFactory
import com.example.beatsfit.viewmodel.UserViewModel
import com.example.beatsfit.room.data.UserDatabase
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        setContent {
            BeatsFitTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setStatusBarColor(
                    color = Color(0XFF0f191f),
                    darkIcons = false
                )

                val context = LocalContext.current
                val navController = rememberNavController()
                val beatsfitViewModel: BeatsfitViewModel = viewModel()
                val locationViewModel: LocationViewModel = viewModel()
                val locationUtils = LocationUtils(context)
                val userViewModel: UserViewModel = viewModel(
                    factory = UserViewModelFactory(UserRepository(UserDatabase.getInstance(context).userDao()))
                )
                AppNavGraph(
                    context = context,
                    navController = navController,
                    beatsfitViewModel = beatsfitViewModel,
                    locationViewModel = locationViewModel,
                    userViewModel = userViewModel,
                    locationUtils = locationUtils
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavGraph(
    context: Context,
    navController: NavHostController,
    beatsfitViewModel: BeatsfitViewModel,
    locationViewModel: LocationViewModel,
    userViewModel: UserViewModel,
    locationUtils: LocationUtils
) {
    val googleSignInClient = createGoogleSignInClient(context)
    val account = GoogleSignIn.getLastSignedInAccount(context)

    NavHost(navController, startDestination = "permission") {

        composable("initiator") {
            Initiator(
                context = context,
                googleSignInClient = googleSignInClient,
                navController = navController,
                viewModel = userViewModel
            )
        }

        composable("permission") {
            if (account != null) {
                PermissionScreen(
                    context = context,
                    account = account,
                    navController= navController,
                )
            }
        }

        composable(
            route = "home_screen/{isPermissionGranted}",
            arguments = listOf(navArgument("isPermissionGranted") {
                type = NavType.BoolType
            })
        ) { backStackEntry ->
            val isPermissionGranted = backStackEntry.arguments?.getBoolean("isPermissionGranted")
            account?.let {
                if (isPermissionGranted != null) {
                    FitnessScreen(
                        context = context,
                        account = it,
                        navController = navController,
                        beatsfitViewModel = beatsfitViewModel,
                        userViewModel = userViewModel,
                        isPermissionGranted = isPermissionGranted
                    )
                }
            }
        }

        composable("new_user") {
            account?.let {
                NewUser(
                    context = context,
                    navController = navController,
                    userViewModel,
                    googleSignInClient
                )
            }
        }

        composable("location_screen/{friendsId}") {backStackEntry->
            val friendsId = backStackEntry.arguments?.getString("friendsId")
            if (friendsId != null) {
                MapsScreen(
                    context = context,friendsId
                )
            }
        }

        composable("friends") {
            account?.let {
                FriendsScreen(navController, account)
            }
        }

        composable("health") {
            account?.let {
                HealthDetailScreen(
                    context = context,
                    account = it,
                    navController = navController,
                    beatsfitViewModel = beatsfitViewModel,
                    locationViewModel = locationViewModel,
                    locationUtils = locationUtils
                )
            }
        }

        composable("graphDetail") {
            GraphDetailScreen(
                title = "title",
                chartData = listOf(7800f, 8200f, 7600f, 8500f, 8900f, 9100f, 8700f)
            )
        }

        composable("members") {
            account?.let {
                Members(account = it, navController = navController, context = context,userViewModel)
            }
        }

        composable("user_profile") {
            account?.let {
                UserProfileScreen(navController, context,userViewModel)
            }
        }
        composable("healthAndFitness") {
            account?.let {
                HealthAndFitness(navController,userViewModel)
            }
        }
        composable("trackFamily") {
            account?.let {
                val id=account.id
                TrackFamily(navController,userViewModel,context,id)
            }
        }
        composable("monitor_details/{friendsId}") {backStackEntry->
            val friendsId = backStackEntry.arguments?.getString("friendsId")
            if (friendsId != null) {
                MonitorDetailsScreen(
                    context = context,
                    friendsId=friendsId,
                    navController=navController
                )
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
