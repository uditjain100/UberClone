package udit.programmer.co.uberclone

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_wel_come.*
import kotlinx.coroutines.*
import org.json.JSONObject
import udit.programmer.co.uberclone.Remote.RetrofitClient
import java.lang.Runnable

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
                                MarkerOptions().position(curr)
//                                    .icon(
//                                        BitmapDescriptorFactory.fromResource(
//                                            R.drawable.car
//                                        )
//                                    )
                                    .title("Current Position")
                            )
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr))
                    }
                }
            }
        }
    }
    private lateinit var handler: Handler
    private val drawPathRunnable = object : Runnable {
        override fun run() {
            if (index < polylineList.size - 1) {
                index++
                next = index + 1
            }
            if (index < polylineList.size - 1) {
                startPosition = polylineList.get(index)
                endPosition = polylineList.get(next)
            }

            val valueAnimator = ValueAnimator.ofInt(0, 100)
            valueAnimator.setDuration(2000)
            valueAnimator.setInterpolator(LinearInterpolator())
            valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(valueAnimator: ValueAnimator?) {
                    v = valueAnimator?.animatedFraction
                    lng =
                        v!!.times(endPosition.longitude) + (1 - v!!).times(startPosition.longitude)
                    lat = v!!.times(endPosition.latitude) + (1 - v!!).times(startPosition.latitude)
                    val newPos = LatLng(lat, lng)
                    carMarker.position = newPos
                    carMarker.setAnchor(0.5f, 0.5f)
                    carMarker.rotation = getBearing(startPosition, newPos)
//                    mMap.moveCamera(
//                        CameraUpdateFactory.newCameraPosition(
//                            object : CameraPosition.Builder()
//                                .target(newPos)
//                                .zoom(15.5f)
//                                .build()
//                        )
//                    )
                }
            })
            valueAnimator.start()
            handler.postDelayed(this, 3000)
        }
    }

    //    Car Animations
    lateinit var polylineList: ArrayList<LatLng>
    lateinit var destination: String
    lateinit var currentPosition: LatLng
    lateinit var startPosition: LatLng
    lateinit var endPosition: LatLng
    lateinit var carMarker: Marker
    lateinit var polyLineOptions: PolylineOptions
    lateinit var blackpolyLineOptions: PolylineOptions
    lateinit var graypolyLine: Polyline
    lateinit var blackpolyLine: Polyline
    var index: Int = 0
    var next: Int = 0
    var v: Float? = 0.0f
    var lat: Double = 0.0
    var lng: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wel_come)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        polylineList = ArrayList()
        btn_go.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                destination = search_edit_text.text.toString().replace(" ", "+")
                Log.d("Creased Meteor", destination)
                getDirection()
            }
        })
    }

    private fun getBearing(startPosition: LatLng, endPosition: LatLng): Float {
        lat = Math.abs(startPosition.latitude - endPosition.latitude)
        lng = Math.abs(startPosition.longitude - endPosition.longitude)
        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < startPosition.longitude) {
            return Math.toDegrees(Math.atan(lng / lat)).toFloat()
        } else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < startPosition.longitude) {
            return ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90).toFloat()
        } else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= startPosition.longitude) {
            return (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat()
        } else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= startPosition.longitude) {
            return ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270).toFloat()
        }
        return -1f
    }

    private fun getDirection() {

        currentPosition = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
        var requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                "mode=driving&" +
                "transit_routing_preference=less_driving&" +
                "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                "destination=" + destination + "&" +
                "key=" + resources.getString(R.string.google_direction_api)
        Log.d("CreasedMentor", requestApi)
        GlobalScope.launch(Dispatchers.Main) {
            var response =
                withContext(Dispatchers.IO) {
                    RetrofitClient.google_api.getPath(requestApi).execute()
                }
            if (response.isSuccessful) {
                var json_Array = JSONObject(response.body().toString()).getJSONArray("routes")
                for (i in 0..json_Array.length()) {
                    var poly =
                        json_Array.getJSONObject(i).getJSONObject("overview_polyline")
                    var polyline = poly.getString("points")
                    polylineList = decodePoly(polyline)
                }
            }

            //Adusting Bounds
            val builder = LatLngBounds.Builder()
            polylineList.forEach { builder.include(it) }
            val bounds = builder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

            polyLineOptions = PolylineOptions()
            polyLineOptions.color(Color.GRAY)
            polyLineOptions.width(5f)
            polyLineOptions.startCap(SquareCap())
            polyLineOptions.endCap(SquareCap())
            polyLineOptions.jointType(JointType.ROUND)
            polyLineOptions.addAll(polylineList)
            graypolyLine = mMap.addPolyline(polyLineOptions)

            blackpolyLineOptions = PolylineOptions()
            blackpolyLineOptions.color(Color.BLACK)
            blackpolyLineOptions.width(5f)
            blackpolyLineOptions.startCap(SquareCap())
            blackpolyLineOptions.endCap(SquareCap())
            blackpolyLineOptions.jointType(JointType.ROUND)
            blackpolyLine = mMap.addPolyline(blackpolyLineOptions)

            mMap.addMarker(
                MarkerOptions().position(polylineList.get(polylineList.size - 1))
                    .title("PickUp Location")
            )

            //Animator
            var polyLineAnimator = ValueAnimator.ofInt(0, 100)
            polyLineAnimator.setDuration(2000)
            polyLineAnimator.setInterpolator(LinearInterpolator())
            polyLineAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(valueAnimator: ValueAnimator?) {
                    var pnts: List<LatLng> = graypolyLine.points
                    var percentValue = valueAnimator?.getAnimatedValue() as Int
                    var size = pnts.size
                    var new_pnts = size * (percentValue / 100.0f).toInt()
                    var sbLst = pnts.subList(0, new_pnts)
                    blackpolyLine.points = sbLst
                }
            })
            polyLineAnimator.start()
            carMarker = mMap.addMarker(
                MarkerOptions().flat(true).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.car)
                )
            )
            handler = Handler()
            index = -1
            next = 1
            handler.postDelayed(drawPathRunnable, 3000)
        }
    }

    //Copied from "https://github.com/irenenaya/Kotlin-Practice/blob/master/MapsRouteActivity.kt"
    private fun decodePoly(encoded: String): ArrayList<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
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
                    mMap.clear()
                    handler.removeCallbacks(drawPathRunnable)
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

    @SuppressLint("MissingPermission")
    private fun displayLocationUpdates() {
        val providers = locationManager.getProviders(true)
        for (i in providers.indices.reversed()) {
            mLastLocation = locationManager.getLastKnownLocation(providers[i])
            if (mLastLocation != null) break
        }
        mLastLocation?.let {
            mCurrent = mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                    .title("Last Location")
            )
            mMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        mLastLocation!!.latitude,
                        mLastLocation!!.longitude
                    )
                )
            )
        }
//        mLastLocation = fusedLocationProviderClient.lastLocation.result
//        if (mLastLocation != null && location_switch.isChecked) {
//            mCurrent.remove()
//            mCurrent = mMap.addMarker(
//                MarkerOptions()
//                    .position(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
//                    .title("Current Location")
//            )
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-34.0, 151.0)))
//        }
        this.RotateMarker(mCurrent, -360f, mMap)
    }

    private fun RotateMarker(mCurrent: Marker, i: Float, mMap: GoogleMap) {
        val start = SystemClock.uptimeMillis()
        val startRotation = mCurrent.rotation
        val duration: Long = 1500
        handler = Handler()
        val interpolator = LinearInterpolator()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val time_elapsed = SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation((time_elapsed / duration).toFloat())
                val rotation = t * i + (1 - t) * startRotation
                mCurrent.rotation = if (-rotation > 180) rotation / 2 else rotation
                if (t < 1.0) handler.postDelayed(this, 16)
            }
        }, 1000)
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