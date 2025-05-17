package com.example.beatsfit.model

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.beatsfit.viewmodel.LocationViewModel
import com.example.beatsfit.util.LocationUtils
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.SleepStages
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException


suspend fun fetchFitnessData(
    context: Context,
    account: GoogleSignInAccount,
    dataType: DataType,
    field: Field
): Float = withContext(Dispatchers.IO) {
    try {
        val dataSet = Fitness.getHistoryClient(context, account)
            .readDailyTotal(dataType)
            .await()
        dataSet.dataPoints.lastOrNull()?.getValue(field)?.let {
            when (field.format) {
                Field.FORMAT_FLOAT -> it.asFloat()
                Field.FORMAT_INT32 -> it.asInt().toFloat()
                else -> 0f
            }
        } ?: 0f
    } catch (e: Exception) {
        Log.e("GoogleFit", "Failed to fetch $dataType data", e)
        0f
    }
}


suspend fun fetchSleepData(
    context: Context,
    account: GoogleSignInAccount
): Map<String, String> {
    val endTime = System.currentTimeMillis()
    val startTime = endTime - TimeUnit.DAYS.toMillis(1)

    val SLEEP_STAGE_NAMES = arrayOf(
        "Unused",
        "Awake (during sleep)",
        "Sleep",
        "Out-of-bed",
        "Light sleep",
        "Deep sleep",
        "REM sleep"
    )

    val sessionRequest = SessionReadRequest.Builder()
        .readSessionsFromAllApps()
        .includeSleepSessions()
        .read(DataType.TYPE_SLEEP_SEGMENT)
        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
        .build()

    val response = Fitness.getSessionsClient(context, account)
        .readSession(sessionRequest)
        .await()

    var totalSleepSummary = "No sleep session found."
    val sleepSegments = StringBuilder()

    for (session in response.sessions) {
        val sessionStart = session.getStartTime(TimeUnit.MILLISECONDS)
        val sessionEnd = session.getEndTime(TimeUnit.MILLISECONDS)

        totalSleepSummary = "${formatTime(sessionStart)} to ${formatTime(sessionEnd)}"

        val dataSets = response.getDataSet(session)
        if (dataSets.isEmpty()) {
            sleepSegments.append("No sleep segments available.\n")
        } else {
            for (dataSet in dataSets) {
                for (point in dataSet.dataPoints) {
                    val sleepStageVal = point.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt()
                    val sleepStage = SLEEP_STAGE_NAMES.getOrNull(sleepStageVal) ?: "Unknown"
                    val segmentStart = point.getStartTime(TimeUnit.MILLISECONDS)
                    val segmentEnd = point.getEndTime(TimeUnit.MILLISECONDS)

                    sleepSegments.append("* $sleepStage: ${formatTime(segmentStart)} to ${formatTime(segmentEnd)}\n")
                }
            }
        }
    }

    return mapOf(
        "totalSleep" to totalSleepSummary,
        "sleepSegment" to sleepSegments.toString().trim()
    )
}

suspend fun fetchHeartRate(
    context: Context,
    account: GoogleSignInAccount
): Float = withContext(Dispatchers.IO) {
    try {
        val fiveMinutesAgo = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)
        val request = DataReadRequest.Builder()
            .read(DataType.TYPE_HEART_RATE_BPM)
            .setTimeRange(fiveMinutesAgo, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()

        val response = Fitness.getHistoryClient(context, account)
            .readData(request)
            .await()
        response.getDataSet(DataType.TYPE_HEART_RATE_BPM)
            .dataPoints
            .lastOrNull()
            ?.getValue(Field.FIELD_BPM)
            ?.asFloat() ?: 0f
    } catch (e: Exception) {
        Log.e("GoogleFit", "Failed to fetch heart rate", e)
        0f
    }
}

suspend fun fetchLocationData(
    viewModel: LocationViewModel,
    locationUtils: LocationUtils
): Pair<Double, Double> = withContext(Dispatchers.IO) {

    locationUtils.requestLocationUpdates(viewModel)
    delay(1000)
    viewModel.location.value?.let {
        withTimeout(3000) {
            while (it == null) {
                delay(1000)
            }
        }
        it.latitude to it.longitude
    } ?: (0.0 to 0.0)
}

fun fetchGoogleFitData(
    context: Context,
    account: GoogleSignInAccount,
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    onDataReceived: (Float, Float, Float, Float, Double, Double, Map<String, String>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val steps = fetchFitnessData(context, account, DataType.TYPE_STEP_COUNT_DELTA, Field.FIELD_STEPS)
            val sleepData=fetchSleepData(context,account)
            val distance = fetchFitnessData(context, account, DataType.TYPE_DISTANCE_DELTA, Field.FIELD_DISTANCE)
            val heartRate = fetchHeartRate(context, account)
            val calories = fetchFitnessData(context, account, DataType.TYPE_CALORIES_EXPENDED, Field.FIELD_CALORIES)
            val (latitude, longitude) = fetchLocationData(viewModel, locationUtils)

            withContext(Dispatchers.Main) {
                onDataReceived(steps, distance, heartRate, calories, latitude, longitude, sleepData)
            }
        } catch (e: Exception) {
            Log.e("GoogleFit", "Error fetching data", e)
        }
    }
}
fun formatTime(millis: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}

