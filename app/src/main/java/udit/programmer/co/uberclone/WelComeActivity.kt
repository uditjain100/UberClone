package udit.programmer.co.uberclone

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.delay

class WelComeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mCurrent: Marker
    private var mLastLocation: Location? = null
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private val locationRequest by lazy {
        LocationRequest()
            .setInterval(2000)
            .setFastestInterval(2000)
            .setSmallestDisplacement(1f)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val curr = LatLng(location.latitude, location.longitude)
                    if (::mMap.isInitialized) {
                        mCurrent =
                            mMap.addMarker(
                                MarkerOptions().position(curr).icon(
                                    BitmapDescriptorFactory.fromResource(
                                        R.drawable.car
                                    )
                                )
                                    .title("Current Position")
                            )
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr))
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wel_come)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        requestFineLocation()
        super.onStart()
        if (isPermissionGranted())
            if (isLocationProviderEnabled()) switchCheckFunction() else showDialog()
        else this.requestFineLocation()
    }

    private fun switchCheckFunction() {
        location_switch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    startLocationUpdates()
                    displayLocationUpdates()
                    Toast.makeText(this@WelComeActivity, "You are Online", Toast.LENGTH_LONG)
                        .show()
                } else {
                    stopLocationUpdates()
                    Toast.makeText(this@WelComeActivity, "You are Offline", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            999 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationProviderEnabled()) switchCheckFunction() else showDialog()
            }
            else -> Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun displayLocationUpdates() {
        mLastLocation = fusedLocationProviderClient.lastLocation.result
        if (mLastLocation != null && location_switch.isChecked) {
            mCurrent.remove()
            mCurrent = mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                    .title("Current Location")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-34.0, 151.0)))
        }
    }

    private fun stopLocationUpdates() {
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationCallback)
    }

    private fun isLocationProviderEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun requestFineLocation() {
        this.requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 999
        )
    }

    private fun isPermissionGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("GPS Enabled")
            .setMessage("GPS should be enabled")
            .setCancelable(true)
            .setPositiveButton("Enable Now", { dialogInterface: DialogInterface?, which: Int ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialogInterface?.dismiss()
            }).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))
        mMap.setIndoorEnabled(true)
        mMap.isTrafficEnabled = true
        mMap.isBuildingsEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.setMaxZoomPreference(40f)

        mCurrent = mMap.addMarker(
            MarkerOptions()
                .position(LatLng(-34.0, 151.0))
                .title("Sydney")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-34.0, 151.0)))
    }
}