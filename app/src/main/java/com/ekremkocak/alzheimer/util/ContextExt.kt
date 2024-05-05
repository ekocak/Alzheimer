package com.ekremkocak.alzheimer.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.ekremkocak.alzheimer.service.LocationTrackingService

fun Context.hasBackgroundLocationPermission() =
    (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED)

fun Context.isLocationServiceRunning(): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (LocationTrackingService::class.java.name == service.service.className) {
            return true
        }
    }
    return false
}