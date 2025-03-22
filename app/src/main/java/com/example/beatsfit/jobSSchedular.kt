package com.example.beatsfit

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.util.Log
import com.example.locationapp.LocationUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

@SuppressLint("SpecifyJobSchedulerIdRange")
class SyncJobService : JobService() {

     override fun onStartJob(params: JobParameters?): Boolean {
        val context = applicationContext

        FirebaseApp.initializeApp(context)

        val userId = "user1"
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val formattedDateTime = dateFormat.format(calendar.time)


        val account = GoogleSignIn.getLastSignedInAccount(context)

        if (account != null) {
            // Initialize ViewModel and fetch health data
            val viewModel = BeatsfitViewModel()
            val locationUtils = LocationUtils(context)
            val locationViewModel = LocationViewModel() // Ensure you have this implemented properly

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
                val maxRetries = 5 // Set a limit on retries to prevent infinite loop

                // Retry fetching health data until valid location is available
                while (!isLocationValid && retryCount < maxRetries) {
                    viewModel.startFetchingHealthData(context, account, locationUtils, locationViewModel)
                    Log.d("SyncJob", "Attempting to fetch valid location data: Attempt $retryCount")
                    delay((2.0.pow(retryCount) * 1000L).toLong()) // Exponential backoff

                    retryCount++

                    val healthData = try {
                        withTimeout(6000) {
                            viewModel.healthData.first { data ->
                                data.latitude != 0.0 && data.longitude != 0.0
                            }
                        }
                    } catch (e: TimeoutCancellationException) {
                        Log.e("Timeout", "Fetching data timed out", e)
                        continue
                    }

                    isLocationValid = healthData.latitude != 0.0 && healthData.longitude != 0.0

                    if (isLocationValid) {
                        saveHealthDataToFirestore(
                            userId = userId,
                            formattedDateTime = formattedDateTime,
                            steps = healthData.steps,
                            distance = healthData.distance,
                            heartRate = healthData.heartRate,
                            calories = healthData.calories,
                            latitude = healthData.latitude,
                            longitude = healthData.longitude
                        )
                        Log.d("SyncJob", "Data saved successfully.")
                    } else {
                        Log.d("SyncJob", "Invalid location data, retrying...")
                    }
                }

                // Notify JobScheduler that the job is done (success or failure after retries)
                jobFinished(params, !isLocationValid)

                
            } catch (e: Exception) {
                Log.e("com.example.beatsfit.SyncJobService", "Error fetching data", e)
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
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}
