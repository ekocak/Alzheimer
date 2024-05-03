package com.ekremkocak.alzheimer.worker

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ekremkocak.alzheimer.R
import com.ekremkocak.alzheimer.service.LocationTrackingService
import com.ekremkocak.alzheimer.util.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay

class LocationTrackingWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val TAG = "LocationTrackingWorker"
    private fun sendNotification(deger : String) {
        val channelId = "location_tracking_channel_work"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("WorkManager Notification")
            .setContentText(deger)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
        notificationManager.notify(5, notification)
    }
    override suspend fun doWork(): Result {
        try {
            val locationManager = context.getSystemService(LocationManager::class.java)

            if (context.hasLocationPermission()){
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                sendNotification(location?.latitude!!.toString()+" - "+location?.longitude.toString())
            }


            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in location tracking: ${e.message}")

            return Result.failure()
        }
    }


}