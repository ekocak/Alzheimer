package com.ekremkocak.alzheimer.data.repository

import com.ekremkocak.alzheimer.data.room.LocationDao
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationDao: LocationDao
) {
    fun getLocations() =
        locationDao.getSavedLocations(100)
    suspend fun softDeleteMarks() {
        locationDao.softDeleteAllData()
    }


}