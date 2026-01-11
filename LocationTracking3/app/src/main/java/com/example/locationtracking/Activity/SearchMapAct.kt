package com.example.locationtracking.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.view.LayoutInflater
import android.widget.SearchView
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.R
import com.example.locationtracking.Utils.toast
import com.example.locationtracking.databinding.ActivitySearchMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class SearchMapAct : BaseAct<ActivitySearchMapBinding>() {

    private var add: String? = null
    private lateinit var currentLocation: LatLng
    private var mapBitmap: Bitmap? = null
    private lateinit var map: GoogleMap
    private var address: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var marker: Marker? = null
    var status = "auto"
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivitySearchMapBinding.inflate(layoutInflater)

    override fun initUI() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (hasInternetConnect()) {
            getUserLocation()
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.liveMap) as SupportMapFragment
            mapFragment.getMapAsync { googleMap ->
                map = googleMap
                map.uiSettings.isZoomControlsEnabled = true
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                map.uiSettings.isMyLocationButtonEnabled = false
                map.uiSettings.setAllGesturesEnabled(true)
                map.setOnCameraIdleListener {
                    map.setOnMapLoadedCallback {
                        val callback =
                            GoogleMap.SnapshotReadyCallback { snapshot ->
                                mapBitmap = snapshot
                                "mapBitmap $mapBitmap".log()
                            }
                        googleMap.snapshot(callback)
                    }
                    val visibleRegion = map.projection.visibleRegion
                    val centerLatLng = visibleRegion.latLngBounds.center
                    val geocoder =
                        Geocoder(this@SearchMapAct, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        centerLatLng.latitude, centerLatLng.longitude, 1
                    )
                    marker?.isVisible = false
                    if (addresses!!.isNotEmpty() && !addresses.equals(null)) {
                        val address = addresses[0]
                        add = address.getAddressLine(0)
                        latitude = address.latitude
                        longitude = address.longitude
                        val markerOptions =
                            MarkerOptions().position(map.cameraPosition.target)
                        marker = map.addMarker(markerOptions)
                    } else {
                        "null data".log()
                    }
                }
                onMapReady(map)
            }
        } else "no internet connection".toast(this@SearchMapAct)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location = binding.searchView.query.toString()
                var addressList: List<Address>? = null
                if (!location.equals(null) && location != "") {
                    val geocoder = Geocoder(this@SearchMapAct)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (addressList!!.isNotEmpty() && !addressList.equals(null)) {
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng, 14f
                            )
                        )
                    } else {
                        "invalid location".toast(this@SearchMapAct)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })


        initClick()
    }

    private fun initClick() {
        binding.apply {

            imgBack.setOnClickListener { finish() }

            mapFocus.setOnClickListener {
                getUserLocation()
            }

        }
    }


    @SuppressLint("MissingPermission")
    fun onMapReady(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = false

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            val currentLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))

            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationProvider = LocationManager.NETWORK_PROVIDER
            val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider)
            if (lastKnownLocation != null) {
                val currentLatLng1 = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng1, 14f))
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getUserLocation() {
        if (hasInternetConnect()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result

                    ("location = $location").log()

                    if (location == null) {
                        ("location is null == ").log()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        if (status == "manual") map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation, 14f
                            )
                        )
                        if (!location.equals(null)) {
                            "here".log()
                            val geocoder = Geocoder(this, Locale.getDefault())
                            val list: List<Address> =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                            binding.apply {
                                address = list[0].getAddressLine(0)
                                latitude = list[0].latitude
                                longitude = list[0].longitude
                            }
                            val latLng = LatLng(list[0].latitude, list[0].longitude)
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng, 14f
                                )
                            )
                            "Latitude${latitude} || Lang${longitude} || ${list[0].locality} || ${
                                list[0].getAddressLine(
                                    0
                                )
                            }".log()
                        }
                    }

                }
            } else {
                "Please turn on location".toast(this)
            }
        } else "no internet connection".toast(this)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

}