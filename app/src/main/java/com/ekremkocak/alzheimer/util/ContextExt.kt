package com.ekremkocak.alzheimer.util

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.ekremkocak.alzheimer.service.LocationTrackingService
fun Context.hasLocationPermissions(): Boolean {
    // Android 10'dan önceki sürümlerde ACCESS_BACKGROUND_LOCATION iznini kontrol et
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    } else {
        // Android 10 ve sonraki sürümlerde ACCESS_BACKGROUND_LOCATION iznini kontrol et
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }
}

fun Context.hasNotificationPermission(): Boolean {
    // Android 10'dan önceki sürümlerde ACCESS_BACKGROUND_LOCATION iznini kontrol et
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    } else {
        // Android 10 ve sonraki sürümlerde ACCESS_BACKGROUND_LOCATION iznini kontrol et
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }
}

fun Context.isLocationServiceRunning(): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (LocationTrackingService::class.java.name == service.service.className) {
            return true
        }
    }
    return false
}