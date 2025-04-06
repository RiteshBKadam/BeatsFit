package com.example.beatsfit.util

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

fun checkDoc(account: GoogleSignInAccount, onResult: (Boolean) -> Unit) {
    val db = Firebase.firestore
    val userId = account.id ?: "Unknown ID"

    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            onResult(document.exists()) // Return true if the document exists
        }
        .addOnFailureListener {
            onResult(false) // Return false in case of an error
        }
}


fun saveLoginDetails(context: Context, username: GoogleSignInAccount, password: Boolean) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("DOC", username.id)
    editor.putString("IS_THERE_IN_DB", password.toString())
    editor.putBoolean("IS_LOGGED_IN", true)
    editor.apply()
}

fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("IS_LOGGED_IN", false)
}
