package com.ekremkocak.alzheimer.util

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {



    fun saveTrackingEnabled(isEnabled: Boolean) {
            sharedPreferences.edit().putBoolean(Constants.TRACKING_ENABLED, isEnabled).apply()
    }

    fun isTrackingEnabled() =
        sharedPreferences.getBoolean(Constants.TRACKING_ENABLED, true)


}