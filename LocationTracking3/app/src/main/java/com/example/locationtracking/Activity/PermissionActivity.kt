package com.example.locationtracking.Activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.Dialog.SettingsDialog
import com.example.locationtracking.R
import com.example.locationtracking.databinding.ActivityPermissionBinding
import java.util.Locale

class PermissionActivity : BaseAct<ActivityPermissionBinding>() {

    private val permissionId = 2

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityPermissionBinding.inflate(layoutInflater)

    override fun initUI() {
        binding.apply {
            start.setOnClickListener {

                if (checkPermissions()) {
                    if (isLocationEnabled()) {
                        "by".log()
                        startActivity(Intent(this@PermissionActivity, MainActivity::class.java))
                        finish()
                    } else {
                        "Please turn on location".tos()
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                } else {
                    "hy".log()
                    requestPermissions()
                }
//                if (laPermission.all { isPermissionGranted(it) }) {
//                    "111   permission not granted".log()
//                    checkPermission()
////                    startActivity(Intent(this@PermissionActivity,MainActivity::class.java))
////                    finish()
//                } else {
//                    "permission not granted".log()
//                    ActivityCompat.requestPermissions(
//                        this@PermissionActivity,
//                        laPermission, LOCATION_PERMISSION_REQUEST
//                    )
//                }
            }

        }
    }
//
//    private fun checkPermission() {
//        if (Build.VERSION.SDK_INT >= 33) {
//            if (laPermission.all { isPermissionGranted(it) }) {
//                "11 is Granted here".log()
//                startActivity(Intent(this@PermissionActivity,MainActivity::class.java))
//                finish()
//            } else {
////                startActivity(Intent(this, SplashScreen::class.java))
////                finish()
//            }
//        } else {
//            if (laPermission.all { isPermissionGranted(it) }) {
//                "22 is Granted here".log()
//            } else {
////                startActivity(Intent(this, SplashScreen::class.java))
////                finish()
//            }
//        }
//    }

//    private fun isPermissionGranted(permission: String): kotlin.Boolean {
//        return ContextCompat.checkSelfPermission(
//            this, permission
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//       if (requestCode == LOCATION_PERMISSION_REQUEST) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            } else {
//                settings = SettingsDialog(this) {
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    val uri = Uri.fromParts("package", packageName, null)
//                    intent.data = uri
//                    startActivityForResult(intent, 200)
//                }
//            }
//        }
//    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {

            var allPermissionsGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted) {
                "hi".log()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                "permission not granted".log()
            }

//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//
//            }
        }
    }

}