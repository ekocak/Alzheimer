package com.ekremkocak.alzheimer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
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
import com.ekremkocak.alzheimer.util.Constants
import com.ekremkocak.alzheimer.util.Constants.MIN_RANGE_CHANGE
import com.ekremkocak.alzheimer.util.Utils
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationTrackingService : Service() {
    @Inject lateinit var appDatabase: AppDatabase
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var  myLocationClient: MyLocationClient
    private var lastKnownLocation: Location? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        myLocationClient = MyDefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("getString(R.string.location_Tracking)")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        myLocationClient.getLocationUpdates(10L)
            .catch { e -> e.printStackTrace() }
            .onEach {location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updateNotification = notification.setContentText("${getString(R.string.location)}: ($lat, $long)")
                notificationManager?.notify(1, updateNotification.build())

                if(lastKnownLocation == null || isDistanceGreaterThan(lastKnownLocation!!, location,MIN_RANGE_CHANGE))
                    lastKnownLocation = location
                saveLocationToDatabase(location)
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop(){
        stopForeground(true)
        stopSelf()
    }

    private fun saveLocationToDatabase(location: Location) {//i will use flow so view auto update !!
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
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}