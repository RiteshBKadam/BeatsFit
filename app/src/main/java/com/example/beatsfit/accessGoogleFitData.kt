package com.example.beatsfit

import android.content.Context
import android.util.Log
import com.example.locationapp.LocationUtils
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit



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

// Fetch heart rate data
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

// Fetch location data
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
    onDataReceived: (Float, Float, Float, Float, Double, Double) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val steps = fetchFitnessData(context, account, DataType.TYPE_STEP_COUNT_DELTA, Field.FIELD_STEPS)
            val distance = fetchFitnessData(context, account, DataType.TYPE_DISTANCE_DELTA, Field.FIELD_DISTANCE)
            val heartRate = fetchHeartRate(context, account)
            val calories = fetchFitnessData(context, account, DataType.TYPE_CALORIES_EXPENDED, Field.FIELD_CALORIES)
            val (latitude, longitude) = fetchLocationData(viewModel, locationUtils)

            withContext(Dispatchers.Main) {
                onDataReceived(steps, distance, heartRate, calories, latitude, longitude)
            }
        } catch (e: Exception) {
            Log.e("GoogleFit", "Error fetching data", e)
        }
    }
}

