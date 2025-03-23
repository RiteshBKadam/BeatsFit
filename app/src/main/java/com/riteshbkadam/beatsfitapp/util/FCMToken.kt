package com.riteshbkadam.beatsfitapp.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging


fun GenereateToken(userId: String){
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result

            saveTokenToFirestore(userId = userId, fcmToken = token)
        } else {
        }
    }

}

fun saveTokenToFirestore(userId: String, fcmToken: String) {

    val db = FirebaseFirestore.getInstance()
    val userDocRef = db.collection("users").document(userId)

    userDocRef.set(mapOf("fcmToken" to fcmToken), SetOptions.merge())
        .addOnSuccessListener {
        }
        .addOnFailureListener { e ->
        }
}

//
//fun exchangeAuthCodeForAccessToken(
//    authCode: String,
//    onSuccess: (String) -> Unit,
//    onError: (String) -> Unit
//) {
//    val client = OkHttpClient()
//
//    val requestBody = FormBody.Builder()
//        .add("code", authCode)
//        .add("client_id", "289566404435-hh18f1l50jlvgsp36q88gj5rjsdcttgp.apps.googleusercontent.com")
//        .add("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
//        .add("grant_type", "authorization_code")
//        .build()
//
//    val request = Request.Builder()
//        .url("https://oauth2.googleapis.com/token")
//        .post(requestBody)
//        .build()
//
//    client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            onError(e.message ?: "Network error")
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            val responseBody = response.body?.string()
//            if (response.isSuccessful && responseBody != null) {
//                val json = JSONObject(responseBody)
//                val accessToken = json.getString("access_token")
//                onSuccess(accessToken)
//            } else {
//                onError("Token exchange failed")
//            }
//        }
//    })
//}
