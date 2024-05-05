package com.ekremkocak.alzheimer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ekremkocak.alzheimer.databinding.ActivityMainBinding
import com.ekremkocak.alzheimer.service.LocationTrackingService
import com.ekremkocak.alzheimer.util.PrefHelper
import com.ekremkocak.alzheimer.util.isLocationServiceRunning

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefHelper: PrefHelper
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        CoroutineScope(Dispatchers.IO).launch {

            if (!isLocationServiceRunning() && prefHelper.isTrackingEnabled())
                startLocationService()
        }

    }

    fun startLocationService() {
        Intent(applicationContext, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startService(this)
            }else{
                startService(this)
            }

        }
    }

    fun stopLocationService() {
        val serviceIntent = Intent(this, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_STOP
        }
        startService(serviceIntent)
    }
}