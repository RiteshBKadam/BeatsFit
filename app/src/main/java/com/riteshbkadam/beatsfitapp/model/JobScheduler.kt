package com.riteshbkadam.beatsfitapp.model

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshbkadam.beatsfitapp.util.LocationUtils
import com.riteshbkadam.beatsfitapp.viewmodel.BeatsfitViewModel
import com.riteshbkadam.beatsfitapp.viewmodel.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.pow

@SuppressLint("SpecifyJobSchedulerIdRange")
class SyncJobService : JobService() {

     override fun onStartJob(params: JobParameters?): Boolean {
        val context = applicationContext

        FirebaseApp.initializeApp(context)
         val account = GoogleSignIn.getLastSignedInAccount(context)



        val userId = account?.id.toString()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val formattedDateTime = dateFormat.format(calendar.time)




        if (account != null) {
            val viewModel = BeatsfitViewModel()
            val locationUtils = LocationUtils(context)
            val locationViewModel = LocationViewModel()

            fetchDataAndSync(viewModel, account, context, userId, formattedDateTime, locationUtils, locationViewModel, params)
        }

        return true
    }

    private fun fetchDataAndSync(
        viewModel: BeatsfitViewModel,
        account: GoogleSignInAccount,
        context: Context,
        userId: String,
        formattedDateTime: String,
        locationUtils: LocationUtils,
        locationViewModel: LocationViewModel,
        params: JobParameters?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var isLocationValid = false
                var retryCount = 0
                val maxRetries = 5

                // Retry fetching health data until valid location is available
                while (!isLocationValid && retryCount < maxRetries) {
                    viewModel.startFetchingHealthData(context, account, locationUtils, locationViewModel)
                    delay((2.0.pow(retryCount) * 1000L).toLong()) // Exponential backoff

                    retryCount++

                    val healthData = try {
                        withTimeout(6000) {
                            viewModel.healthData.first { data ->
                                data.latitude != 0.0 && data.longitude != 0.0
                            }
                        }
                    } catch (e: TimeoutCancellationException) {
                        continue
                    }
                    if (healthData.heartRate <60){
                        FirebaseFirestore.getInstance().document(userId)
                            .get()
                            .addOnSuccessListener {
                                var addedMembers=it.get("addedMembers") as List<String>
                                var username=it.get("username")
                                if(addedMembers!=null){
                                        sendNotificationToContacts(
                                            playerIds = addedMembers,
                                            message = "$username heartbeat dropped to ${healthData.heartRate}  bpm!",
                                            context = applicationContext
                                        )
                                    }
                            }
                    }

                    isLocationValid = healthData.latitude != 0.0 && healthData.longitude != 0.0

                    if (isLocationValid) {
                        saveHealthDataToFirestore(
                            userId = userId,
                            formattedDateTime = formattedDateTime,
                            steps = healthData.steps,
                            distance =String.format("%.2f", healthData.distance/1000).toFloat(),
                            heartRate = healthData.heartRate,
                            calories = healthData.calories,
                            latitude = healthData.latitude,
                            longitude = healthData.longitude
                        )
                    } else {
                    }
                }

                // Notify JobScheduler that the job is done (success or failure after retries)
                jobFinished(params, !isLocationValid)

                
            } catch (e: Exception) {
                jobFinished(params, true) // Notify failure
            }
        }
    }



    private fun saveHealthDataToFirestore(
        userId: String,
        formattedDateTime: String,
        steps: Float,
        distance: Float,
        heartRate: Float,
        calories: Float,
        latitude: Double,
        longitude: Double
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val userRef = firestore.collection("healthData").document(userId)

        // Save data in different collections
        userRef.collection("steps").document(formattedDateTime).set(mapOf("value" to steps, "timestamp" to formattedDateTime))
        userRef.collection("distance").document(formattedDateTime).set(mapOf("value" to distance, "timestamp" to formattedDateTime))
        userRef.collection("heartRate").document(formattedDateTime).set(mapOf("value" to heartRate, "timestamp" to formattedDateTime))
        userRef.collection("totalCalories").document(formattedDateTime).set(mapOf("value" to calories, "timestamp" to formattedDateTime))
        userRef.collection("latitude").document(formattedDateTime).set(mapOf("value" to latitude, "timestamp" to formattedDateTime))
        userRef.collection("longitude").document(formattedDateTime).set(mapOf("value" to longitude, "timestamp" to formattedDateTime))
        Log.d("SyncJobService", "Data saved to Firestore:")
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}



fun sendNotificationToContacts(
    playerIds: List<String>, // OneSignal IDs
    title: String = "Health Alert 🚨",
    message: String = "Heartbeat is critically low!",
    context: Context
) {
    val jsonBody = JSONObject().apply {
        put("app_id", "36eb4abc-8abc-47a5-b9d7-c578abfb674b")
        put("include_player_ids", JSONArray(playerIds))
        put("headings", JSONObject().put("en", title))
        put("contents", JSONObject().put("en", message))
    }

    val request = object : JsonObjectRequest(
        Method.POST,
        "https://onesignal.com/api/v1/notifications",
        jsonBody,
        { response -> Log.d("OneSignal", "Success: $response") },
        { error -> Log.e("OneSignal", "Error: ${error.message}") }
    ) {
        override fun getHeaders(): Map<String, String> {
            return mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Basic os_v2_app_g3vuvpekxrd2looxyv4kx63hjoyiidwdl2audrv7mdvyf5h5khme7cwqvvvxfldjh4o74auojncmju5meauus2n7mqlpifb5yjugsca"
            )
        }
    }

    Volley.newRequestQueue(context).add(request)
}








