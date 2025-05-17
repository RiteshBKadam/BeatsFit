package com.example.beatsfit.model

data class HealthData(
    val steps: Float,
    val distance: Float,
    val heartRate: Float,
    val calories: Float,
    val latitude: Double,
    val longitude: Double,
    val sleepData: Map<String, String>
)
