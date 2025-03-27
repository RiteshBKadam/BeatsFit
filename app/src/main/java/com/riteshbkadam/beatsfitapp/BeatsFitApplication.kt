package com.riteshbkadam.beatsfitapp

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.riteshbkadam.beatsfitapp.model.SyncJobService

class BeatsFitApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeFirebase()

        scheduleSyncJob()
    }

    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing Firebase", Toast.LENGTH_LONG).show()
        }
    }

    private fun scheduleSyncJob() {
        try {
            val componentName = ComponentName(applicationContext, SyncJobService::class.java)
            val jobInfo = JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build()

            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            val result = jobScheduler.schedule(jobInfo)

            if (result == JobScheduler.RESULT_SUCCESS) {
            } else {
                Toast.makeText(this, "Failed to schedule background sync", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
        }
    }
}