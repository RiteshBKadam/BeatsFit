package com.example.beatsfit.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.beatsfit.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FetchFriendsDetails(friendsId:String): Pair<List<String>, Map<String, List<DocumentSnapshot>>> {
    val firestore = FirebaseFirestore.getInstance()

    val dataMap = remember { mutableStateOf<Map<String, List<DocumentSnapshot>>>(emptyMap()) }

    val collections = listOf( "totalCalories","steps","distance", "heartRate", "latitude", "longitude",)

    LaunchedEffect(Unit) {
        val tempMap = mutableMapOf<String, List<DocumentSnapshot>>()

        collections.forEach { collectionName ->
            firestore.collection("healthData")
                .document(friendsId)
                .collection(collectionName)
                .get()
                .addOnSuccessListener { snapshot ->
                    tempMap[collectionName] = snapshot.documents
                    dataMap.value = tempMap.toMap()
                }
                .addOnFailureListener {
                    Log.e("FIRESTORE", "Error fetching $collectionName", it)
                }
        }
    }
    return Pair(collections, dataMap.value)
}
fun getIconForMetric(metric: String): Int {
    return when (metric) {
        "distance" -> R.drawable.km
        "heartRate" -> R.drawable.heartbeat
        "latitude" -> R.drawable.baseline_location_pin_24
        "longitude" -> R.drawable.baseline_location_pin_24
        "steps" -> R.drawable.steps
        "totalCalories" -> R.drawable.family
        else -> R.drawable.family
    }
}
