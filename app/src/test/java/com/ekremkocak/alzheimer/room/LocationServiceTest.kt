package com.ekremkocak.alzheimer.room

import android.content.Context
import android.location.Location
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ekremkocak.alzheimer.data.model.LocationEntity
import com.ekremkocak.alzheimer.data.room.AppDatabase
import com.ekremkocak.alzheimer.data.room.LocationDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LocationServiceTest {


    private lateinit var locationDao: LocationDao
    private lateinit var appDatabase: AppDatabase



    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        locationDao = appDatabase.locationDao()
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    fun testSaveLocation()  {

        val location = Location("TestProvider").apply {
            latitude = 40.7128
            longitude = -74.0060
            time = System.currentTimeMillis()
        }


        val locationEntity = LocationEntity(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = location.time
        )
       // locationDao.insert(locationEntity)


        val allLocations = locationDao.getSavedLocations(100)
        //assert(allLocations.contains(locationEntity))

    }
}