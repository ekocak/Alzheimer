package com.ekremkocak.alzheimer.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ekremkocak.alzheimer.data.model.LocationEntity
import com.ekremkocak.alzheimer.util.Constants


@Database(
    entities = [
        LocationEntity::class
    ],
    version = 1,
    exportSchema = false
)
//@TypeConverters(DateTimeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao


    companion object {

        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}


