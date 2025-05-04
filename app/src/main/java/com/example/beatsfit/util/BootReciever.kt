package com.example.beatsfit.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.beatsfit.model.LocationService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Start your location tracking service
            LocationService.startService(context) // pass user if needed
        }
    }
}
