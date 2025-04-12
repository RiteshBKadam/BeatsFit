package com.example.beatsfit.util

import java.util.Calendar

fun greetString(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 6..11 -> "Morning"
        in 12..16 -> "Afternoon"
        else ->"Evening"
    }
}