package udit.programmer.co.uberclone

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_welcome.*

class Welcome : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mCurrent: Marker
    lateinit var drivers: DatabaseReference
    //  lateinit var googleApiClient: GoogleApiClient
    val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    //  val fusedLocationApiClient by lazy {
//        LocationServices.FusedLocationApi
//    }
//  val lastLocation by lazy {
//        LocationServices.FusedLocationApi
//    }
    val locationRequest by lazy {
        LocationRequest()
            .setInterval(2000)
            .setFastestInterval(2000)
            .setSmallestDisplacement(1f)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        location_switch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isOnline: Boolean) {
                if (isOnline) {
                    displayLocation()
                    Snackbar.make(mapFragment.view!!, "You are Online", Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    stopLocationListener()
                    mCurrent.remove()
                    Snackbar.make(mapFragment.view!!, "You are Offline", Snackbar.LENGTH_LONG)
                        .show()
                }


            }

        })
//        location_switch.setOnCheckedChangeListener {
//            object : MaterialAnimatedSwitch.OnCheckedChangeListener {
//                override fun onCheckedChanged(isOnline: Boolean) {
//                    if (isOnline) {
//                        startLocationListener()
//                        displayLocation()
//                        Snackbar.make(mapFragment.view!!, "You are Online", Snackbar.LENGTH_LONG)
//                            .show()
//                    } else {
//                        stopLocationListener()
//                        mCurrent.remove()
//                        Snackbar.make(mapFragment.view!!, "You are Offline", Snackbar.LENGTH_LONG)
//                            .show()
//                    }
//                }
//            }
//        }
    }

    override fun onStart() {
        requestFineLocation()
        super.onStart()
        if (!isFineLocationGranted()) {
            showGPS_NotDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            999 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isFineLocationGranted()) {
                    showGPS_NotDialog()
                }
            } else {
                Toast.makeText(this, "Permissions Not Granted", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showGPS_NotDialog() {
        AlertDialog.Builder(this)
            .setMessage("GPS should be Enabled")
            .setTitle("GPS Enabled")
            .setCancelable(false)
            .setPositiveButton("Enable Now", { dialogInterface: DialogInterface?, which: Int ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialogInterface?.dismiss()
            }).show()
    }

    private fun isFineLocationGranted(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestFineLocation() {
        this.requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 999)
    }

//    private fun startLocationListener() {
//
//        fusedLocationApiClient.requestLocationUpdates(
//            googleApiClient, locationRequest, object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    for (location in locationResult.locations) {
//                        val curr = LatLng(location.latitude, location.longitude)
//                        if (::mMap.isInitialized) {
//                            mMap.addMarker(MarkerOptions().position(curr).title("Current Position"))
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(curr))
//                        }
//                    }
//                }
//            },
//            Looper.myLooper()
//        )
//    }

    private fun displayLocation() {

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        val curr = LatLng(location.latitude, location.longitude)
                        if (::mMap.isInitialized) {
                            mCurrent =
                                mMap.addMarker(
                                    MarkerOptions().position(curr).icon(
                                        BitmapDescriptorFactory.fromResource(R.drawable.car)
                                    ).title("Current Position")
                                )
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(curr))
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(20f))
                            mMap.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))
                        }
                    }
                }
            },
            Looper.myLooper()
        )
        rotateMarker(mCurrent, -360, mMap)


    }

    private fun rotateMarker(mCurrent: Marker, i: Int, mMap: GoogleMap) {

        var start = SystemClock.uptimeMillis()
        val startRotation = mCurrent.rotation
        val duration: Long = 1500
        var handler = Handler()
        val interpolator = LinearInterpolator()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val time_elapsed = SystemClock.uptimeMillis() - start
                var t = interpolator.getInterpolation((time_elapsed / duration).toFloat())
                var rotation = t * i + (1 - t) * startRotation
                mCurrent.rotation = if (-rotation > 180) rotation / 2 else rotation
                if (t < 1.0) handler.postDelayed(this, 16)
            }
        }, 1000)

    }

    private fun stopLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val curr = LatLng(location.latitude, location.longitude)
                    if (::mMap.isInitialized) {
                        mCurrent =
                            mMap.addMarker(MarkerOptions().position(curr).title("Current Position"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr))
                    }
                }
            }
        })

//        fusedLocationApiClient.removeLocationUpdates(
//            googleApiClient, object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    for (location in locationResult.locations) {
//                        val curr = LatLng(location.latitude, location.longitude)
//                        if (::mMap.isInitialized) {
//                            mMap.addMarker(MarkerOptions().position(curr).title("Current Position"))
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(curr))
//                        }
//                    }
//                }
//            }
//        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply {
            isMyLocationButtonEnabled = true
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isRotateGesturesEnabled = true
        }
        mMap.setMaxZoomPreference(40f)


        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mCurrent = mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
