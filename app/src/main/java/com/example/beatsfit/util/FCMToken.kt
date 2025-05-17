package com.example.beatsfit.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject


fun GenereateToken(userId: String){
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result
            Log.d("FCM_TOKEN", token)

            saveTokenToFirestore(userId = userId, fcmToken = token)
        } else {
            Log.e("FCM_TOKEN", "Fetching token failed", task.exception)
        }
    }

}

fun saveTokenToFirestore(userId: String, fcmToken: String) {

    val db = FirebaseFirestore.getInstance()
    val userDocRef = db.collection("users").document(userId)

    userDocRef.set(mapOf("fcmToken" to fcmToken), SetOptions.merge())
        .addOnSuccessListener {
            Log.d("Firestore", "FCM token saved")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Failed to save FCM token", e)
        }
}

fun fetchSpO2Data(accessToken: String) {
    val now = System.currentTimeMillis()
    val sevenDaysAgo = now - 7 * 24 * 60 * 60 * 1000

    val jsonBody = """
    {
      "aggregateBy": [{
        "dataTypeName": "com.google.oxygen_saturation"
      }],
      "bucketByTime": { "durationMillis": 86400000 },
      "startTimeMillis": $sevenDaysAgo,
      "endTimeMillis": $now
    }
    """.trimIndent()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate")
        .addHeader("Authorization", "Bearer $accessToken")
        .addHeader("Content-Type", "application/json")
        .post(jsonBody.toRequestBody("application/json".toMediaType()))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("SpO2", "Fetch failed: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            Log.d("SpO2", "Raw response: $body")
            // TODO: Parse SpO2 values from `body`
        }
    })
}

fun exchangeAuthCodeForAccessToken(
    authCode: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val client = OkHttpClient()

    val requestBody = FormBody.Builder()
        .add("code", authCode)
        .add("client_id", "289566404435-hh18f1l50jlvgsp36q88gj5rjsdcttgp.apps.googleusercontent.com")
        .add("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
        .add("grant_type", "authorization_code")
        .build()

    val request = Request.Builder()
        .url("https://oauth2.googleapis.com/token")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError(e.message ?: "Network error")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val accessToken = json.getString("access_token")
                onSuccess(accessToken)
            } else {
                onError("Token exchange failed")
            }
        }
    })
}
