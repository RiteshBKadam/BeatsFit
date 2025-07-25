package com.riteshbkadam.beatsfitapp.view


import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.onesignal.OneSignal
import com.riteshbkadam.beatsfitapp.R
import com.riteshbkadam.beatsfitapp.room.data.User
import com.riteshbkadam.beatsfitapp.util.GenereateToken
import com.riteshbkadam.beatsfitapp.util.formatPhoneNumber
import com.riteshbkadam.beatsfitapp.util.handleSignInResult
import com.riteshbkadam.beatsfitapp.util.rememberImeState
import com.riteshbkadam.beatsfitapp.util.saveLoginDetails
import com.riteshbkadam.beatsfitapp.viewmodel.UserViewModel
import kotlinx.coroutines.launch


@Composable
fun SignUp(
    context: Context,
    navController: NavHostController,
    viewModel: UserViewModel,
    googleSignInClient: GoogleSignInClient
) {
    val mistrully = FontFamily(
        Font(R.font.mistrully, FontWeight.Normal),
    )
    val quicksand= FontFamily(
        Font(R.font.quicksand, FontWeight.Normal)
    )

    var isDataSaved by remember { mutableStateOf(false) }
    var mobileNumber by remember { mutableStateOf("") }
    var showMobileInput by remember { mutableStateOf(false) }
    var currentAccount by remember { mutableStateOf<com.google.android.gms.auth.api.signin.GoogleSignInAccount?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(
                task = task,
                context = context,
                onSuccess = { account ->
                    currentAccount = account
                    val db = Firebase.firestore
                    val userId = account.id ?: "Unknown ID"
                    coroutineScope.launch {
                        contacts= fetchSavedContacts(account)
                    }
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            val newUser = User(
                                id = account.id.toString(),
                                firstName = account.givenName.toString(),
                                lastName = account.familyName.toString(),
                                imageUri = account.photoUrl?.toString(),
                                email = account.email,
                                height = 0,
                                weight = "",
                                gender = "",
                                stepGoal =0,
                                cyclingGoal = 0)

                            viewModel.insertUser(newUser)
                            saveLoginDetails(context, account, true)

                            if (document.exists()) {
                                isDataSaved = true
                                isLoading = false
                                val isPermissionGranted=true
                                navController.navigate("home_screen") {
                                    popUpTo("sign_in") { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                showMobileInput = true
                                isLoading = false
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Failed to check user data: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                },
                onError = { error ->
                    isLoading = false
                    Toast.makeText(context, "Sign-in failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

    LaunchedEffect(Unit) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (showMobileInput && currentAccount != null) {

        var imeState = rememberImeState()
        var scrollState= rememberScrollState()

        LaunchedEffect(key1 = imeState.value) {
            if(imeState.value){
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFF0f191f)),
            horizontalAlignment = Alignment.CenterHorizontally) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.signup))
            val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
            Column(modifier = Modifier.padding(top = 100.dp)
            ){
                LottieAnimation(
                    composition, progress, modifier = Modifier
                        .size(300.dp)
                )
            }
            Row{
                Text("Beats", fontFamily = quicksand, fontSize = 50.sp, color = Color.White)
                Text("Fit", fontFamily = mistrully, fontSize = 48.sp, color = Color.White)

            }
            TextField(
                value = mobileNumber,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                        mobileNumber = newValue
                    }
                },
                label = { Text("Enter Your Mobile Number") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp,50.dp,50.dp, bottom = 0.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (mobileNumber.length == 10) {
                        val db = FirebaseFirestore.getInstance()
                        val account = currentAccount!!
                        val user = hashMapOf(
                            "id" to (account.id ?: "Unknown ID"),
                            "first_name" to (account.givenName ?: ""),
                            "last_name" to (account.familyName ?: ""),
                            "email" to (account.email ?: "Unknown Email"),
                            "mobile_number" to formatPhoneNumber(mobileNumber),
                            "oneSignalId" to OneSignal.User.onesignalId.toString()
                        )

                        db.collection("users")
                            .document(account.id ?: "Unknown ID")
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "User data saved successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isDataSaved = true
                                isLoading = false
                                GenereateToken(account.id.toString())
                                val isPermissionGranted=true
                                navController.navigate("home_screen") {
                                    popUpTo("sign_up") { inclusive = true }
                                }
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Failed to save user data: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Enter a valid 10-digit number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 10.dp)
            ) {
                Text("Verify and Save")
            }
        }
    }
}
