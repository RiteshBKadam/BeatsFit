package com.example.beatsfit

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == "android.net.conn.CONNECTIVITY_CHANGE") {
            Log.d("com.example.beatsfit.BootReceiver", "Device rebooted or network changed")
            rescheduleJobs(context)
        }
    }

    private fun rescheduleJobs(context: Context) {
        val componentName = ComponentName(context, SyncJobService::class.java)
        val jobInfo = JobInfo.Builder(1, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setPeriodic(15 * 60 * 1000)
            .build()

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = jobScheduler.schedule(jobInfo)

        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d("com.example.beatsfit.BootReceiver", "Job rescheduled successfully")
        } else {
            Log.e("com.example.beatsfit.BootReceiver", "Failed to reschedule job")
        }
    }
}
