package com.riteshbkadam.beatsfitapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.NavController
import com.riteshbkadam.beatsfitapp.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


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

fun logout(navController: NavController, context: Context, userViewModel: UserViewModel) {
    val googleSignInClient =
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    userViewModel.clearDatabase()
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("IS_LOGGED_IN",false)
    googleSignInClient.signOut()
        .addOnCompleteListener {

            navController.navigate("initiator") {
                popUpTo("home_screen") { inclusive = true }
            }
        }
}

fun handleSignInResult(
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
        onError(e)
    }
}
