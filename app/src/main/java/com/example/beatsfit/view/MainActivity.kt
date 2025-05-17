package com.example.beatsfit.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.beatsfit.model.LocationService
import com.example.beatsfit.util.isUserLoggedIn
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.firebase.firestore.FirebaseFirestore
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

                LocationService.startService(context)
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

    val firestore=FirebaseFirestore.getInstance()

    var startDestination by remember{ mutableStateOf("") }

    if(!isUserLoggedIn(context)){
        startDestination="initiator"
    }else{
        startDestination="home_screen/true"
    }
    NavHost(navController, startDestination = startDestination) {

        composable("initiator") {
            Initiator(
                context = context,
                googleSignInClient = googleSignInClient,
                navController = navController,
                viewModel = userViewModel
            )
        }

        composable(
            route = "home_screen/{isPermissionGranted}",
            arguments = listOf(navArgument("isPermissionGranted") {
                type = NavType.BoolType
            })
        ) { backStackEntry ->
            val isPermissionGranted = backStackEntry.arguments?.getBoolean("isPermissionGranted")
            Toast.makeText(context,"main", Toast.LENGTH_LONG).show()
            val account = GoogleSignIn.getLastSignedInAccount(context)
            account?.let{
                if (isPermissionGranted != null) {
                    HomeScreen(
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

        composable("sign_up") {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            account?.let {
                SignUp(
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
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                FriendsScreen(navController,account,userViewModel)
            }
        }

        composable("health") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                HealthDetailScreen(
                    context = context,
                    account = it,
                    navController = navController,
                    beatsfitViewModel = beatsfitViewModel,
                    locationViewModel = locationViewModel,
                    locationUtils = locationUtils,
                    userViewModel = userViewModel
                )
            }
        }
        composable("graphDetail/{title}") {backStackEntry->
            val title = backStackEntry.arguments?.getString("title")
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                GraphDetailScreen(
                    title = title.toString(),
                    navController,
                    context,
                    userViewModel,
                    it
                )
            }
        }
        composable("members") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                Members(account = it, navController = navController, context = context,userViewModel)
            }
        }

        composable("user_profile") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                UserProfileScreen(navController, context,it,userViewModel)
            }
        }
        composable("healthAndFitness") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                HealthAndFitness(navController,userViewModel)
            }
        }
        composable("trackFamily") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

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
        composable("dietAndGoals") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            if (account != null) {
                DietAndGoals(
                    context = context,
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
        }
        composable("emergency") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            if (account != null) {
                EmergencyAndSharing(
                    context = context,
                    navController = navController,
                    account = account,
                    firestore = firestore
                )
            }
        }
        composable("appPreferences") {

                AppPreferences(
                    context = context,
                    navController = navController
                )
            }

        composable(
            route = "liveMap/{phone}/{name}",
            arguments = listOf(
                navArgument("phone") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ){backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone").toString()
            val name = backStackEntry.arguments?.getString("name").toString()
            LiveLocationMap(Contact(name,phone),navController)
        }


    }

}



fun createGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(
            Scope("https://www.googleapis.com/auth/fitness.oxygen_saturation.read")
        )
        .build()
    return GoogleSignIn.getClient(context, gso)
}
