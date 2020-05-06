package udit.programmer.co.uberclone

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.CompoundButton
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_welcome.*

class Welcome : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mCurrent: Marker
    lateinit var googleApiClient: GoogleApiClient
    val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    //    val fusedLocationApiClient by lazy {
//        LocationServices.FusedLocationApi
//    }
//    val lastLocation by lazy {
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
                            mMap.addMarker(MarkerOptions().position(curr).title("Current Position"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(curr))
                        }
                    }
                }
            },
            Looper.myLooper()
        )

    }

    private fun stopLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val curr = LatLng(location.latitude, location.longitude)
                    if (::mMap.isInitialized) {
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
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
