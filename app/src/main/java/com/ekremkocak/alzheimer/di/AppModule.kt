package com.ekremkocak.alzheimer.di

import android.content.Context
import android.content.SharedPreferences
import com.ekremkocak.alzheimer.util.Constants
import com.ekremkocak.alzheimer.util.PrefHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    fun providePrefHelper(sharedPreferences: SharedPreferences): PrefHelper {
        return PrefHelper(sharedPreferences)
    }
}