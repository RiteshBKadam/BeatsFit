package com.example.beatsfit.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.beatsfit.R
import com.example.beatsfit.room.data.User
import com.example.beatsfit.util.isUserLoggedIn
import com.example.beatsfit.util.saveLoginDetails
import com.example.beatsfit.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun Initiator(
    context: Context,
    googleSignInClient: GoogleSignInClient,
    navController: NavController,
    viewModel: UserViewModel
) {
    var signInError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(
            task = task,
            context = context,
            onSuccess = { account ->
                val db = Firebase.firestore
                val userId = account.id ?: "Unknown ID"
                isLoading = true
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val newUser= User(
                            id = account.id.toString(),
                            firstName = account.givenName.toString(),
                            lastName = account.familyName.toString(),
                            imageUri = account.photoUrl?.toString(),
                            email = account.email,
                            height = 0,
                            weight = "",
                            gender = ""
                        )
                        viewModel.insertUser(newUser)
                        saveLoginDetails(context,account,true)

                        if (document.exists()) {
                            isLoading = false
                            navController.navigate("home_screen")

                        } else {
                            isLoading = false
                            navController.navigate("new_user")
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        signInError = "Error checking user: ${e.message}"
                    }
            },
            onError = { error ->
                isLoading = false
                signInError = error.message
            }
        )
    }
    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0f191f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Lottie Animation
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rhisss))
                    LottieAnimation(
                        composition,
                        modifier = Modifier.size(350.dp),
                        iterations = 1
                    )
                    Text(
                        text = "BeatsFit",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.padding(3.dp))

                    // Subtitle
                    Text(
                        text = "Connects you with your family!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.padding(10.dp))


                    Button(
                        onClick = {
                                val signInIntent = googleSignInClient.signInIntent
                                launcher.launch(signInIntent)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text(
                            text = if (isLoading) "Connecting..." else "Connect to Google Fit",
                            fontSize = 16.sp
                        )
                    }
                    signInError?.let { error ->
                        Spacer(Modifier.padding(8.dp))
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    )
}

private fun handleSignInResult(
    task: Task<GoogleSignInAccount>,
    context: Context,
    onSuccess: (GoogleSignInAccount) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val account = task.getResult(ApiException::class.java)
        if (account != null) {
            onSuccess(account)
        }
    } catch (e: ApiException) {
        Log.e("GoogleSignIn", "Sign-in failed: ${e.statusCode}")
        onError(e)
    }
}
fun isFirstBoot(context: Context): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isFirstBoot", true)
}
fun setFirstBootDone(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean("isFirstBoot", false).apply()
}