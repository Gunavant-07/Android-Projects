package com.example.locationtracking.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import com.example.locationtracking.Activity.Application.Companion.getString
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.Activity.Application.Companion.putString
import com.example.locationtracking.Adapter.MembersAdapter
import com.example.locationtracking.Adapter.SpinnerAdapter
import com.example.locationtracking.Dialog.DeleteUserDialog
import com.example.locationtracking.Dialog.DirectionDialog
import com.example.locationtracking.Dialog.LogoutDialog
import com.example.locationtracking.Dialog.MemberDataDialog
import com.example.locationtracking.ModelData.AllUserid
import com.example.locationtracking.ModelData.Circledata
import com.example.locationtracking.ModelData.MemberData
import com.example.locationtracking.ModelData.MemberID
import com.example.locationtracking.Receiver.BatteryReceiver
import com.example.locationtracking.Receiver.LocationStatusReceiver
import com.example.locationtracking.Utils.delayInMillis
import com.example.locationtracking.Utils.loadImg
import com.example.locationtracking.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.Locale


class MainActivity : BaseAct<ActivityMainBinding>(), OnMapReadyCallback,
    BatteryReceiver.BatteryChangeListener {


    private var firstName: String = ""
    private lateinit var headerView: View
    private var DialogClcik = false
    private var Dusername = ""
    private var Duserid = ""
    private var Dcity = ""
    private var Dcharge = ""
    private var Dnetwork = ""
    private var Daddress = ""
    private var Demail = ""
    private var Dimage = ""
    private var charging = ""
    private var networkType = ""
    private var resultHandler: ActivityResultLauncher<Intent>? = null
    private var requestCode = -1

    private var Scirclename = ""
    private var marker2 = LatLng(0.0, 0.0)
    private var marker1 = LatLng(0.0, 0.0)
    var cityName = ""
    var address = ""
    private var userposition = 0
    private var click = false
    private var Userid = ""
    private var position = 0
    private var urlcode = 100
    private var alluserid: Any? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var map: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationStatusReceiver: LocationStatusReceiver
    private var mapView: MapView? = null
    lateinit var membersAdapter: MembersAdapter
    lateinit var spinnerAdapter: SpinnerAdapter

    val globalUserList: MutableList<MemberData> = mutableListOf()
    var memberlist = ArrayList<MemberData>()
    var memberidlist = ArrayList<MemberID>()
    var circlelist = ArrayList<Circledata>()
    var alluseridlist = ArrayList<AllUserid>()
    val database = FirebaseDatabase.getInstance()
    val reference = database.getReference("Users")
    var firebaseAuth = Firebase.auth
    var auth = FirebaseAuth.getInstance()
    var user = firebaseAuth.currentUser
    private var locationCallback: LocationCallback? = null
    private lateinit var batteryReceiver: BatteryReceiver

    //    var user: FirebaseUser? = null
//    var auth: FirebaseAuth? = null

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            "onAvailable:::Network ::: onAvailable: $network".log()

            networkType = getNetworkType(this@MainActivity)
            reference.child(user?.uid!!)!!.child("network").setValue(networkType)
        }

        override fun onLost(network: Network) {
            super.onLost(network)

            networkType = getNetworkType(this@MainActivity)
            reference.child(user?.uid!!)!!.child("network").setValue(networkType)
//            binding.isNetworkCheck.text = networkType + "Off"

            "onAvailable:::Network ::: onAvailable: $network".log()
        }


        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unMetered =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            "onCapabilitiesChanged::unMetered::${unMetered}".log()
//            Toast.makeText(this@MainActivity, network.toString(), Toast.LENGTH_LONG).show()
        }
    }


    @SuppressLint("MissingPermission")
    override fun initUI() {
        binding.apply {

            memberidlist.clear()
            globalUserList.clear()
            val lat = getString("lat")
            val lon = getString("lon")

            "lat: $lat || lon: $lon".log()

            if (lat == "" && lon == "") {

            } else {
                latitude = lat.toDouble()
                longitude = lon.toDouble()
            }


            membersAdapter = MembersAdapter(this@MainActivity, globalUserList, 100, {}, {}, {})
            checkNetworkConnection()
            isConnected(this@MainActivity)

            batteryReceiver = BatteryReceiver(this@MainActivity)

            val mapFragment =
                supportFragmentManager.findFragmentByTag("map_fragment") as SupportMapFragment
            mapFragment.getMapAsync(this@MainActivity)
            runOnUiThread {
                locationStatusReceiver = LocationStatusReceiver {
                    getLocation()
                }
            }

            val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            registerReceiver(locationStatusReceiver, intentFilter)
            checkPermissions()

            mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this@MainActivity)

            mapView = MapView(this@MainActivity)

            actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, myDrawerLayout, 0, 0)
            myDrawerLayout.addDrawerListener(actionBarDrawerToggle!!)
            // Display the hamburger icon to launch the drawer
            // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
            actionBarDrawerToggle!!.syncState()
            getLocation()
            btnmenu.setOnClickListener {
                myDrawerLayout.openDrawer(navView)
            }
            delayInMillis(1000) {
                runOnUiThread {
                    "Location Null after few Seconds".log()
                    getLocation()
                    getUserLocation1()
                }
            }

            rlAddMember.setOnClickListener {
                startActivity(Intent(this@MainActivity, CreateMemberActivity::class.java))
            }
            rlJoinGrp.setOnClickListener {
                startActivity(Intent(this@MainActivity, JoinMemberActivity::class.java))
            }
            rlSearch.setOnClickListener {
                startActivity(Intent(this@MainActivity, SearchMapAct::class.java))
            }
            rlLogout.setOnClickListener {
                LogoutDialog(this@MainActivity) {
                    auth.signOut()
                    myDrawerLayout.close()
                    "User Logout successfully".tos()
                    startActivity(Intent(this@MainActivity, LoginScreen::class.java))
                    finish()
                }
            }


            setSpinnerView()

            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childrensnapshots in snapshot.children) {
                            childrensnapshots.children.forEach {
                                if (it.key == "userId") {
                                    alluserid = it.value
                                }
                            }

                            if (user?.uid == alluserid) {

                                val userid =
                                    childrensnapshots.child("userId").getValue(String::class.java)

                                val name =
                                    childrensnapshots.child("name").getValue(String::class.java)

                                val issharing =
                                    childrensnapshots.child("issharing")
                                        .getValue(Boolean::class.java)
                                val imgurl =
                                    childrensnapshots.child("imageUrl")
                                        .getValue(String::class.java)
                                val lat =
                                    childrensnapshots.child("lat")
                                        .getValue(Double::class.java)
                                val lng =
                                    childrensnapshots.child("lng")
                                        .getValue(Double::class.java)

                                if (userid != null && name != null && issharing != null && imgurl != null && lat != null && lng != null) {


                                    include.profileName.text = name
                                    firstName = name
                                    if (firstName.length > 8) {
                                        // Display only the first 8 letters followed by "..."
                                        val truncatedText: String =
                                            firstName.substring(0, 8) + "..."
                                        txtname.text = truncatedText
                                    } else {
                                        txtname.text = firstName
                                    }

                                }
                            }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    "$error".log()
                }

            })
            val storageRef = Firebase.storage.reference.child("images/${user?.uid}_profile.jpg")
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    // Handle the download URL
                    val imageUrl = uri.toString()
                    imguser.loadImg(imageUrl)
//                    val headerImageView: ImageView = headerView.findViewById(R.id.imgprofile)
                    include.imgprofile.loadImg(imageUrl)
                    include.imgprofile.setOnClickListener {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                ProfileAct::class.java
                            ).putExtra("name", firstName).putExtra("url", imageUrl)
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    "Error getting download URL: $exception".log()
                }

            currentuser.setOnClickListener {
                map.clear()
                map.uiSettings.isMyLocationButtonEnabled = false
                getLocation()
                reference.child(user?.uid!!)!!.child("lat").setValue(latitude)
                reference.child(user?.uid!!).child("lng").setValue(longitude)
                map.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("You"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f))
            }

            reference.child(user?.uid!!).child("Circles").get().addOnSuccessListener { snapshot ->
                var circleMemberId = ""
                circlelist.clear()
                circlelist.add(Circledata("Your Circle"))
                circlelist.add(Circledata("Create Group"))
                circlelist.add(Circledata("Join Group"))
                for (childSnapshot in snapshot.children) {
//                    if (childSnapshot.key.toString() == "Your") {
//                        "is your".log()
//                    } else {
//                    }
                    circleMemberId = childSnapshot.key.toString()
                    circlelist.add(Circledata(circleMemberId))
                    setSpinnerView()
                }

            }.addOnFailureListener { "added user = =${it}".log() }

            firbaseDataSetup()
        }
    }

    private fun setSpinnerView() {
        binding.apply {
            if (circlelist.isEmpty()) {
                circlelist.clear()
                circlelist.add(Circledata("Your Circles"))
                circlelist.add(Circledata("Create Circle"))
                circlelist.add(Circledata("Join Circle"))
            }
            val adapter = SpinnerAdapter(this@MainActivity, circlelist)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    Scirclename = circlelist[p2].circlename

                    if (Scirclename == "Create Group") {
                        startActivity(Intent(this@MainActivity, CreateMemberActivity::class.java))
                        p0!!.setSelection(0)
                    } else if (Scirclename == "Join Group") {
                        startActivity(Intent(this@MainActivity, JoinMemberActivity::class.java))
                        p0!!.setSelection(0)
                    }
                    firbaseDataSetup()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    "on nothing selected spinner".log()
                }

            }
        }
    }

    fun firbaseDataSetup() {
        memberidlist.clear()
        globalUserList.clear()
        reference.child(user?.uid!!).child("Circles").get().addOnSuccessListener { snapshot ->
            var circleName = ""
            for (childSnapshot in snapshot.children) {
                circleName = childSnapshot.key.toString()
                childSnapshot.children.forEach {

                    if (circleName == Scirclename) {
                        val circlememberId = it.child("circlememberid").getValue(String::class.java)
                        val circleMember = MemberID(circlememberId.toString())
                        memberidlist.add(circleMember)
                    }
                }
            }
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        globalUserList.clear()
                        for (childrensnapshot in snapshot.children) {
                            memberidlist.forEach {

                                val membersids = it.memberid

                                if (membersids == childrensnapshot.key) {
                                    val userid =
                                        childrensnapshot.child("userId")
                                            .getValue(String::class.java)
                                    val email =
                                        childrensnapshot.child("email")
                                            .getValue(String::class.java)
                                    val name =
                                        childrensnapshot.child("name")
                                            .getValue(String::class.java)
                                    val issharing =
                                        childrensnapshot.child("issharing")
                                            .getValue(Boolean::class.java)
                                    val imgurl =
                                        childrensnapshot.child("imageUrl")
                                            .getValue(String::class.java)
                                    val latitude =
                                        childrensnapshot.child("lat")
                                            .getValue(Double::class.java)
                                    val longitude =
                                        childrensnapshot.child("lng")
                                            .getValue(Double::class.java)
                                    val city =
                                        childrensnapshot.child("city")
                                            .getValue(String()::class.java)
                                    val charge =
                                        childrensnapshot.child("charge")
                                            .getValue(String()::class.java)
                                    val network =
                                        childrensnapshot.child("network")
                                            .getValue(String()::class.java)

                                    val address =
                                        childrensnapshot.child("address")
                                            .getValue(String()::class.java)

                                    if (userid != null && name != null && issharing != null && imgurl != null && latitude != null && longitude != null
                                        && city != null && charge != null && network != null && address != null && email != null
                                    ) {
                                        val userData =
                                            MemberData(
                                                userid,
                                                latitude,
                                                longitude,
                                                city,
                                                charge,
                                                network,
                                                issharing,
                                                address,
                                                email,
                                                name,
                                                imgurl
                                            )
                                        memberlist.add(userData)
                                        globalUserList.clear()
                                        globalUserList.addAll(listOf(userData))
                                    }
                                }
                            }
                        }
                    }
                    membersAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    "$error".log()
                }
            })

        }.addOnFailureListener { "added user = =${it}".log() }
        membersAdapter =
            MembersAdapter(this@MainActivity, globalUserList, urlcode, onGet = {
                Userid = globalUserList[it].userid
                userposition = it
                "click".log()
                click = true
                Dusername = globalUserList[userposition].username
                Duserid = globalUserList[userposition].userid
                Dcity = globalUserList[userposition].city
                Dcharge = globalUserList[userposition].charge
                Dnetwork = globalUserList[userposition].network
                Daddress = globalUserList[userposition].address
                Demail = globalUserList[userposition].email
                Dimage = globalUserList[userposition].imgurl
                setlocationzoom()
                ("click - - $click").log()
                reference.child(user?.uid!!)!!.child("lat").setValue(latitude)
                reference.child(user?.uid!!).child("lng").setValue(longitude)

            }, onDatachange = {
                if (urlcode == 100) {
                    urlcode = 0
                }
            }, onClick = {
                position = it
                reference.child(user?.uid!!)!!.child("lat").setValue(latitude)
                reference.child(user?.uid!!).child("lng").setValue(longitude)
                if (globalUserList[position].userid == Userid) {
                    map.clear()
                    map.uiSettings.isMyLocationButtonEnabled = false
//                            map.uiSettings.setAllGesturesEnabled(false)

                    val userLocations = listOf(
                        LatLng(latitude, longitude),
                        LatLng(
                            globalUserList[userposition].lat,
                            globalUserList[userposition].lon
                        ),
                    )

                    for (i in 1 until minOf(userLocations.size, 2)) {
                        val location = userLocations[i]
                        map.addMarker(
                            MarkerOptions().position(location)
                                .title("Location ${i + 1}")
                        )
                    }

                    marker1 = userLocations[0]
                    marker2 = userLocations[1]

                    map.addMarker(MarkerOptions().position(marker1).title("You"))
                    map.addMarker(MarkerOptions().position(marker2).title("Other"))

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker2, 16f))

                    reference.child(user?.uid!!)!!.child("lat").setValue(latitude)
                    reference.child(user?.uid!!).child("lng").setValue(longitude)
                    openDialog(
                        Dusername,
                        Duserid,
                        Dcity,
                        Dcharge,
                        Dnetwork,
                        Daddress,
                        Demail,
                        Dimage
                    )

                }
            })
        binding.rcymember.adapter = membersAdapter
    }

    fun setlocationzoom() {
        map.uiSettings.isMyLocationButtonEnabled = false
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker2, 12f))
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation1() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                ("Location Update Main Act: ${p0.lastLocation?.latitude}").log()
            }
        }
        mFusedLocationClient.requestLocationUpdates(
            getLocationRequest(), locationCallback as LocationCallback, null
        )
    }

    private fun getLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.numUpdates = 1
        return locationRequest
    }

    fun openDialog(
        username: String,
        userid: String,
        city: String,
        charge: String,
        network: String,
        address: String,
        email: String,
        image: String
    ) {
        MemberDataDialog(
            this@MainActivity,
            username,
            userid,
            city,
            charge,
            network,
            image,
            onClick = {
                "User Removed From Circle".tos()
                DeleteUserDialog(this@MainActivity) {
                    reference.child(userid).child("Circles").child(Scirclename).child(user?.uid!!)
                        .removeValue()
                    reference.child(user?.uid!!).child("Circles").child(Scirclename).child(userid)
                        .removeValue()
                    urlcode = 100
                    recreate()
                }

            },
            onDirection = {
                DirectionDialog(this@MainActivity) {
                    val uri =
                        "https://www.google.com/maps/dir/?api=1&origin=${marker1.latitude},${marker1.longitude}&destination=${marker2.latitude},${marker2.longitude}"

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")

                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent) // Launch Google Maps
                    } else {
                        Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }) {
            val dlat = marker2.latitude
            val dlng = marker2.longitude

            startActivity(
                Intent(this@MainActivity, MemberProfileActivity::class.java).putExtra(
                    "username",
                    username
                )
                    .putExtra("city", city).putExtra("address", address)
                    .putExtra("network", network).putExtra("charge", charge)
                    .putExtra("lat", dlat).putExtra("lng", dlng).putExtra("email", email)
                    .putExtra("imageurl", image)
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                val locationRequest = LocationRequest.create().apply {
                    interval = 1000
                    fastestInterval = 1000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                mFusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(p0: LocationResult) {
                            for (location in p0.locations) {
                                latitude = location.latitude
                                longitude = location.longitude
                                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                                try {
                                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                                    if (addresses!!.isNotEmpty()) {
                                        cityName = addresses[0].locality
                                        address = addresses[0].getAddressLine(0)
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                                putString("lat", latitude.toString())
                                putString("lon", longitude.toString())

                                reference.child(user?.uid!!).child("city").setValue(cityName)
                                reference.child(user?.uid!!).child("address").setValue(address)
                                reference.child(user?.uid!!).child("lat")
                                    .setValue(location.latitude)
                                reference.child(user?.uid!!).child("lng")
                                    .setValue(location.longitude)
                            }
                        }
                    }, Looper.getMainLooper()
                )
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        } else {
            requestPermissions()
        }
    }

    private fun calculateDistance(latlng1: LatLng, latlng2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            latlng1.latitude,
            latlng1.longitude,
            latlng2.latitude,
            latlng2.longitude,
            results
        )
        return results[0].toDouble()
    }

    // Function to calculate zoom level based on distance
    private fun getZoomLevel(distance: Double): Float {
        val equatorLength = 40075004.0 // in meters
        val widthInPixels = resources.displayMetrics.widthPixels
        val metersPerPixel = equatorLength / 256
        val zoomLevel = (16 - Math.log(metersPerPixel * distance) / Math.log(2.0)).toFloat()
        return if (zoomLevel > 21) 21f else zoomLevel
    }

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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        map.uiSettings.isMyLocationButtonEnabled = false
        getLocation()
        reference.child(user?.uid!!)!!.child("lat").setValue(latitude)
        reference.child(user?.uid!!).child("lng").setValue(longitude)
        map.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("You"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun checkNetworkConnection() {
        try {
            try {
                var networkResult: NetworkRequest? = null
                networkResult = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build()
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                networkResult?.let { connectivityManager.requestNetwork(it, networkCallback) }
            } catch (e: Exception) {
                e.printStackTrace()
                registerActivityForCallback()
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package: $packageName")
                startActivityForResultOfActivity(intent, 100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            registerActivityForCallback()

            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package: $packageName")
            startActivityForResultOfActivity(intent, 100)
        }
    }

    private fun startActivityForResultOfActivity(intent: Intent, requestCode: Int) {
        this.requestCode = requestCode
        if (resultHandler != null) {
            resultHandler?.launch(intent)
        }
    }

    private fun registerActivityForCallback() {
        resultHandler =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(requestCode, result.resultCode, result.data)
                requestCode = -1
            }
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager: ConnectivityManager
        var networkInfo: NetworkInfo? = null
        try {
            connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            networkInfo = connectivityManager.activeNetworkInfo
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    // Function to get network type
    private fun getNetworkType(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return if (networkInfo != null) {
            when (networkInfo.type) {
                ConnectivityManager.TYPE_WIFI -> "Wi-Fi"
                ConnectivityManager.TYPE_MOBILE -> "Mobile"
                else -> "OFF"
            }
        } else {
            "OFF"
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }

    override fun onBatteryPercentageChanged(percentage: Int) {
        charging = "${percentage}%"
        reference.child(user?.uid!!)!!.child("charge").setValue(charging)
    }

}