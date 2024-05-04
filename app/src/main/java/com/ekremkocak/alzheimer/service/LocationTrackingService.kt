package com.ekremkocak.alzheimer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ekremkocak.alzheimer.MainActivity
import com.ekremkocak.alzheimer.R
import com.ekremkocak.alzheimer.data.model.LocationEntity
import com.ekremkocak.alzheimer.data.room.AppDatabase
import com.ekremkocak.alzheimer.util.Constants.MIN_RANGE_CHANGE
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var appDatabase: AppDatabase
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val shouldUseGPS: Boolean = true
    private val shouldUseNetwork: Boolean = true
    private val shouldUsePassive: Boolean = true
    private lateinit var locationManager: LocationManager
    private var lastKnownLocation: Location? = null
    private val minTimeBetweenUpdates: Long = 5 * 60 * 1000.toLong()
    private val minDistanceBetweenUpdates: Float = 100f
    private var isListening = false
        private set


    @SuppressLint("SuspiciousIndentation")
    private val listener = LocationListener { location ->
        Location(location).let { currentLocation ->
            CoroutineScope(Dispatchers.IO).launch {
                if(lastKnownLocation == null || isDistanceGreaterThan(lastKnownLocation!!, currentLocation, MIN_RANGE_CHANGE))
                lastKnownLocation = currentLocation
                updateNotification(currentLocation.time.toString())
                saveLocationToDatabase(location)
            }
        }
    }
    private fun saveLocationToDatabase(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            val locationDao = appDatabase.locationDao()
            val locationEntity = LocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = System.currentTimeMillis()
            )
            locationDao.insert(locationEntity)
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService()
        } else {
            startForeground(1, Notification())
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerForLocationUpdates(provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider, minTimeBetweenUpdates, minDistanceBetweenUpdates, listener)
        } else {
            //listeners.forEach { l -> l.onProviderError(ProviderError("Provider `$provider` is not enabled")) }
        }
    }

    private fun startListening(context: Context) {
        initManagerAndUpdateLastKnownLocation(context)
        if (!isListening) {
            if (shouldUseGPS) {
                registerForLocationUpdates(LocationManager.GPS_PROVIDER)
            }
            if (shouldUseNetwork) {
                registerForLocationUpdates(LocationManager.NETWORK_PROVIDER)
            }
            if (shouldUseNetwork) {
                registerForLocationUpdates(LocationManager.PASSIVE_PROVIDER)
            }
            isListening = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun initManagerAndUpdateLastKnownLocation(context: Context) {

        locationManager = context.getSystemService(LocationManager::class.java)

        if (lastKnownLocation == null && shouldUseGPS) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (lastKnownLocation == null && shouldUseNetwork) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if (lastKnownLocation == null && shouldUsePassive) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        }
    }

    private fun start() {
        startListening(applicationContext)
    }

    private fun stop() {
        stopLocationUpdates()
        stopSelf()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(listener)
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val channelId = "location_tracking_channel"
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Tracking Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Tracking")
            .setContentText("Tracking your location...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)


        startForeground(1, notificationBuilder.build())
    }

    private fun updateNotification(contentText: String) {
        notificationBuilder.setContentText(contentText)
        notificationManager.notify(1, notificationBuilder.build())
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
    private fun isDistanceGreaterThan(location1: Location, location2: Location, distanceThreshold: Float): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude,
            location1.longitude,
            location2.latitude,
            location2.longitude,
            results
        )
        return results[0] > distanceThreshold
    }

}