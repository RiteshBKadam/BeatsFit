//import android.annotation.SuppressLint
//import android.app.job.JobParameters
//import android.app.job.JobService
//import android.content.Context
//import android.util.Log
//import com.example.beatsfit.BeatsfitViewModel
//import com.example.locationapp.LocationUtils
//import com.example.beatsfit.LocationViewModel
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.fitness.Fitness
//import com.google.android.gms.fitness.data.DataType
//import com.google.android.gms.fitness.data.Field
//import com.google.android.gms.fitness.request.DataReadRequest
//import com.google.firebase.FirebaseApp
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.TimeoutCancellationException
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import kotlinx.coroutines.withTimeout
//import java.text.SimpleDateFormat
//import java.util.*
//import kotlin.math.pow
//
//@SuppressLint("SpecifyJobSchedulerIdRange")
//class SyncJobService : JobService() {
//
//    override fun onStartJob(params: JobParameters?): Boolean {
//        val context = applicationContext
//
//        // Initialize Firebase
//        FirebaseApp.initializeApp(context)
//
//        // Prepare required data
//        val userId = "user1"
//        val calendar = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
//        val formattedDateTime = dateFormat.format(calendar.time)
//
//        // Get the signed-in Google account
//        val account = GoogleSignIn.getLastSignedInAccount(context)
//
//        if (account != null) {
//            // Initialize ViewModel and fetch health data
//            val viewModel = BeatsfitViewModel()
//            val locationUtils = LocationUtils(context)
//            val locationViewModel = LocationViewModel() // Ensure you have this implemented properly
//
//            // Launch coroutine to fetch and sync data
//            CoroutineScope(Dispatchers.IO).launch {
//                fetchDataAndSync(viewModel, account, context, userId, formattedDateTime, locationUtils, locationViewModel, params)
//            }
//        }
//
//        return true // Indicate that job is still running
//    }
//
//    private suspend fun fetchDataAndSync(
//        viewModel: BeatsfitViewModel,
//        account: GoogleSignInAccount,
//        context: Context,
//        userId: String,
//        formattedDateTime: String,
//        locationUtils: LocationUtils,
//        locationViewModel: LocationViewModel,
//        params: JobParameters?
//    ) {
//        val maxRetries = 5
//        val initialDelay = 1000L // Start with 1 second delay
//        var currentRetry = 0
//
//        while (currentRetry < maxRetries) {
//            try {
//                // Start fetching health data
//                viewModel.startFetchingHealthData(context, account, locationUtils, locationViewModel)
//
//                // Wait for valid data (timeout after 6 seconds)
//                val healthData = withTimeout(6000L) {
//                    viewModel.healthData.first { it.latitude != 0.0 && it.longitude != 0.0 }
//                }
//
//                // Save data if valid
//                saveHealthDataToFirestore(
//                    userId = userId,
//                    formattedDateTime = formattedDateTime,
//                    steps = healthData.steps,
//                    distance = healthData.distance,
//                    heartRate = healthData.heartRate,
//                    calories = healthData.calories,
//                    latitude = healthData.latitude,
//                    longitude = healthData.longitude
//                )
//
//                Log.d("SyncJob", "Data saved successfully.")
//                jobFinished(params, false) // Job is finished successfully
//                return // Exit the loop on success
//
//            } catch (e: TimeoutCancellationException) {
//                Log.e("SyncJob", "Fetching data timed out, retrying...", e)
//            } catch (e: Exception) {
//                Log.e("SyncJob", "Error fetching data: ${e.message}", e)
//            }
//
//            // Exponential backoff before retrying
//            val backoffDelay = initialDelay * 2.0.pow(currentRetry).toLong()
//            Log.d("SyncJob", "Retrying in $backoffDelay ms...")
//            delay(backoffDelay)
//
//            currentRetry++
//        }
//
//        // If max retries reached, mark the job as failed
//        Log.e("SyncJob", "Max retries reached. Job failed.")
//        jobFinished(params, true) // Reschedule the job
//    }
//
//    private fun saveHealthDataToFirestore(
//        userId: String,
//        formattedDateTime: String,
//        steps: Float,
//        distance: Float,
//        heartRate: Float,
//        calories: Float,
//        latitude: Double,
//        longitude: Double
//    ) {
//        val firestore = FirebaseFirestore.getInstance()
//        val userRef = firestore.collection("healthData").document(userId)
//
//        // Save data in different collections
//        userRef.collection("steps").document(formattedDateTime).set(mapOf("value" to steps, "timestamp" to formattedDateTime))
//        userRef.collection("distance").document(formattedDateTime).set(mapOf("value" to distance, "timestamp" to formattedDateTime))
//        userRef.collection("heartRate").document(formattedDateTime).set(mapOf("value" to heartRate, "timestamp" to formattedDateTime))
//        userRef.collection("totalCalories").document(formattedDateTime).set(mapOf("value" to calories, "timestamp" to formattedDateTime))
//        userRef.collection("latitude").document(formattedDateTime).set(mapOf("value" to latitude, "timestamp" to formattedDateTime))
//        userRef.collection("longitude").document(formattedDateTime).set(mapOf("value" to longitude, "timestamp" to formattedDateTime))
//    }
//
//    override fun onStopJob(params: JobParameters?): Boolean {
//        return false
//    }
//}
