package com.example.beatsfit.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.beatsfit.viewmodel.UserViewModel
import com.example.beatsfit.room.data.User
import com.example.beatsfit.util.formatPhoneNumber
import com.example.beatsfit.util.saveLoginDetails
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun NewUser(
    context: Context,
    account: GoogleSignInAccount,
    navController: NavHostController,
    viewModel: UserViewModel
) {
    var isDataSaved by remember { mutableStateOf(false) }
    var mobileNumber by remember { mutableStateOf("") }

    if (!isDataSaved) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (mobileNumber.length == 10) {

                    val db = Firebase.firestore
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
                            if (!isDataSaved) {
                                Toast.makeText(context, "User data saved successfully!", Toast.LENGTH_SHORT).show()
                                isDataSaved = true
                                val newUser= User(
                                    account.id.toString(),
                                    account.givenName.toString(),
                                    account.familyName.toString(), account.photoUrl?.toString(),account.email,0,"","")
                                viewModel.insertUser(newUser)
                                saveLoginDetails(context,account,true)
                                navController.navigate("home_screen") {
                                    popUpTo("sign_in") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.d("NewUser", e.toString())
                        }
                } else {
                    Toast.makeText(context, "Enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Verify and Save")
            }
        }
    }
}
