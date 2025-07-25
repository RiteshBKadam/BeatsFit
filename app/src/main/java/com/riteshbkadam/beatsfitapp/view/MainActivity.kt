package com.riteshbkadam.beatsfitapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshbkadam.beatsfitapp.model.LocationService
import com.riteshbkadam.beatsfitapp.room.data.UserDatabase
import com.riteshbkadam.beatsfitapp.room.data.UserRepository
import com.riteshbkadam.beatsfitapp.ui.theme.BeatsFitTheme
import com.riteshbkadam.beatsfitapp.util.LocationUtils
import com.riteshbkadam.beatsfitapp.util.UserViewModelFactory
import com.riteshbkadam.beatsfitapp.util.isUserLoggedIn
import com.riteshbkadam.beatsfitapp.viewmodel.BeatsfitViewModel
import com.riteshbkadam.beatsfitapp.viewmodel.LocationViewModel
import com.riteshbkadam.beatsfitapp.viewmodel.UserViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        setContent {
            BeatsFitTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setStatusBarColor(Color(0xFF0f191f), darkIcons = false)

                val context = LocalContext.current
                val navController = rememberNavController()

                val beatsfitViewModel: BeatsfitViewModel = viewModel()
                val locationViewModel: LocationViewModel = viewModel()
                val userViewModel: UserViewModel = viewModel(
                    factory = UserViewModelFactory(UserRepository(UserDatabase.getInstance(context).userDao()))
                )

                val locationUtils = remember { LocationUtils(context) }
                val firestore = remember { FirebaseFirestore.getInstance() }

                var isPermissionGranted by remember { mutableStateOf(false) }

                // Permission launcher
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val allGranted = permissions.all { it.value }
                    isPermissionGranted = allGranted
                    if (allGranted) {
                        LocationService.startService(context)
                    } else {
                        Toast.makeText(context, "Location permissions required", Toast.LENGTH_LONG).show()
                    }
                }

                LaunchedEffect(Unit) {
                    checkAndRequestPermissions(context, permissionLauncher) { granted ->
                        isPermissionGranted = granted
                        if (granted) LocationService.startService(context)
                    }
                }

                AppNavGraph(
                    context = context,
                    navController = navController,
                    beatsfitViewModel = beatsfitViewModel,
                    locationViewModel = locationViewModel,
                    userViewModel = userViewModel,
                    locationUtils = locationUtils,
                    firestore = firestore,
                    isPermissionGranted = isPermissionGranted
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
    locationUtils: LocationUtils,
    firestore: FirebaseFirestore,
    isPermissionGranted: Boolean
) {
    val googleSignInClient = createGoogleSignInClient(context)

    val account = GoogleSignIn.getLastSignedInAccount(context)

    val firestore=FirebaseFirestore.getInstance()



    NavHost(navController, startDestination = "splash") {
        composable("request_permissions") {
            PermissionsScreen(navController)
        }
        composable("splash") {
            Splash(navController, isUserLoggedIn(context))
        }
        composable("initiator") {
            Initiator(
                context = context,
                googleSignInClient = googleSignInClient,
                navController = navController,
                viewModel = userViewModel
            )
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

        composable(route = "home_screen") {
            val context = LocalContext.current
            val account = GoogleSignIn.getLastSignedInAccount(context)
            val isPermissionGranted = remember { mutableStateOf(false) }

            account?.let {
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
        composable("trackFamily") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                val id=account.id
                TrackFamily(navController,userViewModel,context,id)
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
        composable("friends") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                FriendsScreen(navController,account,userViewModel)
            }
        }
        composable("members") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                Members(account = it, navController = navController, context = context,userViewModel)
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


        composable("user_profile") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                UserProfileScreen(navController, context,it,userViewModel)
            }
        }
        composable("healthAndFitness") {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            account?.let {
                HealthAndFitness(navController, userViewModel)
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
fun checkAndRequestPermissions(
    context: Context,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    onResult: (Boolean) -> Unit
) {
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        
    )

    val notGranted = permissions.filter {
        ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }

    if (notGranted.isEmpty()) {
        onResult(true)
    } else {
        launcher.launch(permissions)
    }
}
