package com.example.locationtracking.Activity

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.locationtracking.Utils.visible
import com.example.locationtracking.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreen : BaseAct<ActivitySplashScreenBinding>() {



    val laPermission = arrayOf(
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION
    )


    var firebaseAuth = Firebase.auth
    var user =firebaseAuth.currentUser

    override fun getActivityBinding(inflater: LayoutInflater) =  ActivitySplashScreenBinding.inflate(layoutInflater)

    override fun initUI() {
        binding.apply {
            val connectionManager : ConnectivityManager =getSystemService(Context.CONNECTIVITY_SERVICE)as ConnectivityManager
            var activityNetwork: NetworkInfo? = connectionManager.activeNetworkInfo
            val isconnected:Boolean =activityNetwork?.isConnected==true

            if (isconnected){
                user?.uid
                checkPermission()
            }
            else{
                progress.visible()
                "Network Error".tos()
            }
        }
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (laPermission.all { isPermissionGranted(it) } && isLocationEnabled()  ) {
                if(user !=null) {
                    Handler().postDelayed({
                        var intent = Intent(this@SplashScreen,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },3000)
                }
                else {
                    Handler().postDelayed({
                        var intent = Intent(this@SplashScreen, LoginScreen::class.java)
                        startActivity(intent)
                        finish()
                    },3000)
                }
            } else {
                if(user !=null) {
                    Handler().postDelayed({
                        startActivity(Intent(this, PermissionActivity::class.java))
                        finish()
                    },3000)
                }
                else{
                    Handler().postDelayed({
                        var intent = Intent(this@SplashScreen, LoginScreen::class.java)
                        startActivity(intent)
                        finish()
                    },3000)

                }

            }
        } else {
            if (laPermission.all { isPermissionGranted(it) } && isLocationEnabled()) {
                if(user !=null) {
                    Handler().postDelayed({
                        var intent = Intent(this@SplashScreen,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },3000)
                }
                else {
                    Handler().postDelayed({
                        var intent = Intent(this@SplashScreen, LoginScreen::class.java)
                        startActivity(intent)
                        finish()
                    },3000)
                }
            } else {
                if(user !=null) {
                    Handler().postDelayed({
                        startActivity(Intent(this, PermissionActivity::class.java))
                        finish()
                    },3000)
                }
                else{
                    Handler().postDelayed({
                        var intent = Intent(this@SplashScreen, LoginScreen::class.java)
                        startActivity(intent)
                        finish()
                    },3000)

                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun isPermissionGranted(permission: String): kotlin.Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

}