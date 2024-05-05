package com.ekremkocak.alzheimer.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class GeocoderHelper @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun getAddress(lat: Double, lng: Double): List<Address> {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            return@withContext geocoder.getFromLocation(lat, lng, 1)!!
        }
    }
}