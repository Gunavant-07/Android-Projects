package com.example.locationtracking.Receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.*
import com.example.locationtracking.Activity.Application.Companion.log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationStatusReceiver(var onClick: (String) -> Unit) : BroadcastReceiver() {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action: String? = intent.action
        if (action != null && action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isLocationEnabled && networkEnabled) {
                ("Location Start").log()
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        onClick("On")
                        ("Location Update: ${locationResult.lastLocation?.latitude}").log()
                    }
                }
                fusedLocationProviderClient!!.requestLocationUpdates(
                    getLocationRequest(),
                    locationCallback as LocationCallback,
                    null
                )
            } else {
                ("Location Off").log()
                if (fusedLocationProviderClient != null) {
                    onClick("off")
                    fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
                }
            }
        }
    }

    private fun getLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = 100
        locationRequest.priority = PRIORITY_HIGH_ACCURACY
        locationRequest.numUpdates = 1
        return locationRequest
    }
}
