package com.example.beatsfit.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.beatsfit.viewmodel.UserViewModel
import com.example.beatsfit.room.data.User
import com.example.beatsfit.util.formatPhoneNumber
import com.example.beatsfit.util.handleSignInResult
import com.example.beatsfit.util.saveLoginDetails
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

@Composable
fun NewUser(
    context: Context,
    navController: NavHostController,
    viewModel: UserViewModel,
    googleSignInClient: GoogleSignInClient
) {
    var isDataSaved by remember { mutableStateOf(false) }
    var mobileNumber by remember { mutableStateOf("") }
    var showMobileInput by remember { mutableStateOf(false) }
    var currentAccount by remember { mutableStateOf<com.google.android.gms.auth.api.signin.GoogleSignInAccount?>(null) }
    var isLoading by remember { mutableStateOf(true) }

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
                                gender = ""
                            )

                            viewModel.insertUser(newUser)
                            saveLoginDetails(context, account, true)

                            if (document.exists()) {
                                isDataSaved = true
                                isLoading = false
                                val isPermissionGranted=true
                                navController.navigate("home_screen/$isPermissionGranted") {
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
                    Log.e("NewUser", "Sign-in error", error)
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
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {

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
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (mobileNumber.length == 10) {
                        isLoading = true
                        val db = FirebaseFirestore.getInstance()
                        val account = currentAccount!!
                        val user = hashMapOf(
                            "id" to (account.id ?: "Unknown ID"),
                            "first_name" to (account.givenName ?: "Unknown Name"),
                            "last_name" to (account.familyName ?: "Unknown Last Name"),
                            "email" to (account.email ?: "Unknown Email"),
                            "mobile_number" to formatPhoneNumber(mobileNumber)
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
                                val isPermissionGranted=true
                                navController.navigate("home_screen/$isPermissionGranted") {
                                    popUpTo("sign_in") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Failed to save user data: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("NewUser", "Save error", e)
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Enter a valid 10-digit number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verify and Save")
            }
        }
    }
}
