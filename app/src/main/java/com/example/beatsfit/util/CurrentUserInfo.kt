package com.example.beatsfit.util

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

fun CurrentUserInfo(account: GoogleSignInAccount) {

    val currentUser=Firebase.auth.currentUser
    val userFirstName=account.displayName
    val userFamilyName=account.familyName
    val profilePicture=account.photoUrl
    val userMail=account.email
    val uid=account.id

}