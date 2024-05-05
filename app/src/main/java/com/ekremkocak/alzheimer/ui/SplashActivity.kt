package com.ekremkocak.alzheimer.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ekremkocak.alzheimer.MainActivity
import com.ekremkocak.alzheimer.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestAllPermission()
    }

    private fun requestAllPermission() {
        val permissionsToRequest = mutableListOf<String>()

        val permissionFineLocationApproved = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionFineLocationApproved) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val permissionCoarseLocationApproved = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionCoarseLocationApproved) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        val permissionPostNotificationsApproved = ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED)

        if (!permissionPostNotificationsApproved) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permissionBackgroundLocationApproved = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionBackgroundLocationApproved) {
                permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                ALL_PERMISSION_REQUEST_CODE
            )
        } else {
            navigateToMainActivity()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            ALL_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateToMainActivity()
                } else {
                    val showRationale = shouldShowRequestPermissionRationale(permissions[0])
                    if (!showRationale) {
                        Log.d("Permission", "Kullanıcı bir daha sorma seçeneğini seçti.")
                    }else{

                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
        private const val ALL_PERMISSION_REQUEST_CODE = 1002
    }
}