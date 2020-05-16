package udit.programmer.co.uberclone

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import udit.programmer.co.uberclone.Remote.RetrofitClient

class Welcome : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mCurrent: Marker
    var mLastLocation: Location? = null
    lateinit var drivers: DatabaseReference
    val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    val locationRequest by lazy {
        LocationRequest()
            .setInterval(2000)
            .setFastestInterval(2000)
            .setSmallestDisplacement(1f)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
    val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    //Car Animations
//    lateinit var baseUrl: String
//    lateinit var polylineList: ArrayList<LatLng>
//    lateinit var destination: String
//    lateinit var currentPosition: LatLng
//    lateinit var startPosition: LatLng
//    lateinit var endPosition: LatLng
//    lateinit var carMarker: Marker
//    lateinit var polyLineOptions: PolylineOptions
//    lateinit var blackpolyLineOptions: PolylineOptions
//    lateinit var graypolyLine: Polyline
//    lateinit var blackpolyLine: Polyline
//    lateinit var handler: Handler
//    var index: Int = 0
//    var next: Int = 0
//    var v: Float? = 0.0f
//    var lat: Double = 0.0
//    var lng: Double = 0.0
//    var drawPathRunnable = object : Runnable {
//        override fun run() {
//            if (index < polylineList.size - 1) {
//                index++
//                next = index + 1
//            }
//            if (index < polylineList.size - 1) {
//                startPosition = polylineList.get(index)
//                endPosition = polylineList.get(next)
//            }
//            val valueAnimator = ValueAnimator.ofInt(0, 100)
//            valueAnimator.setDuration(2000)
//            valueAnimator.setInterpolator(LinearInterpolator())
//            valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
//                override fun onAnimationUpdate(valueAnimator: ValueAnimator?) {
//                    v = valueAnimator?.animatedFraction
//                    lng =
//                        v!!.times(endPosition.longitude) + (1 - v!!).times(startPosition.longitude)
//                    lat = v!!.times(endPosition.latitude) + (1 - v!!).times(startPosition.latitude)
//                    val newPos = LatLng(lat, lng)
//                    carMarker.position = newPos
//                    carMarker.setAnchor(0.5f, 0.5f)
//                    carMarker.rotation = getBearing(startPosition, newPos)
////                    mMap.moveCamera(
////                        CameraUpdateFactory.newCameraPosition(
////                            object : CameraPosition.Builder()
////                                .target(newPos)
////                                .zoom(15.5f)
////                                .build()
////                        )
////                    )
//                }
//            })
//            valueAnimator.start()
//            handler.postDelayed(this, 3000)
//        }
//    }

    private fun getBearing(startPosition: LatLng, newPos: LatLng): Float {
//        val lat = Math.abs(startPosition.latitude - endPosition.latitude)
//        val lng = Math.abs(startPosition.longitude - endPosition.longitude)
//
//        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < startPosition.longitude) {
//            return Math.toDegrees(Math.atan(lng / lat)).toFloat()
//        } else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < startPosition.longitude) {
//            return ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90).toFloat()
//        } else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= startPosition.longitude) {
//            return (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat()
//        } else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= startPosition.longitude) {
//            return ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270).toFloat()
//        }
        return -1f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        location_switch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
//            override fun onCheckedChanged(buttonView: CompoundButton?, isOnline: Boolean) {
//                if (isOnline) {
//                    displayLocation()
//                    Snackbar.make(mapFragment.view!!, "You are Online", Snackbar.LENGTH_LONG)
//                        .show()
//                } else {
//                    stopLocationListener()
//                    mCurrent.remove()
//                    mMap.clear()
//                    handler.removeCallbacks(drawPathRunnable)
//                    Snackbar.make(mapFragment.view!!, "You are Offline", Snackbar.LENGTH_LONG)
//                        .show()
//                }
//            }
//        })

//        polylineList = ArrayList()
//        btn_go.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                destination = search_edit_text.text.toString().replace(" ", "+")
//                Log.d("CreasedMentor", destination)
//                getDirection()
//            }
//        })
    }

    private fun getDirection() {
//
//        currentPosition = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
//        var requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
//                "mode=driving&" +
//                "transit_routing_preference=less_driving&" +
//                "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
//                "destination=" + destination + "&" +
//                "key=" + resources.getString(R.string.google_direction_api)
//        Log.d("CreasedMentor", requestApi)
//        GlobalScope.launch(Dispatchers.Main) {
//            var response =
//                withContext(Dispatchers.IO) {
//                    RetrofitClient.google_api.getPath(requestApi).execute()
//                }
//            if (response.isSuccessful) {
//                var json_Array = JSONObject(response.body().toString()).getJSONArray("routes")
//                for (i in 0..json_Array.length()) {
//                    var poly =
//                        json_Array.getJSONObject(i).getJSONObject("overview_polyline")
//                    var polyline = poly.getString("points")
//                    polylineList = decodePoly(polyline)
//                }
//            }
//
//            //Adusting Bounds
//            val builder = LatLngBounds.Builder()
//            polylineList.forEach { builder.include(it) }
//            val bounds = builder.build()
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))
//
//            polyLineOptions = PolylineOptions()
//            polyLineOptions.color(Color.GRAY)
//            polyLineOptions.width(5f)
//            polyLineOptions.startCap(SquareCap())
//            polyLineOptions.endCap(SquareCap())
//            polyLineOptions.jointType(JointType.ROUND)
//            polyLineOptions.addAll(polylineList)
//            graypolyLine = mMap.addPolyline(polyLineOptions)
//
//            blackpolyLineOptions = PolylineOptions()
//            blackpolyLineOptions.color(Color.BLACK)
//            blackpolyLineOptions.width(5f)
//            blackpolyLineOptions.startCap(SquareCap())
//            blackpolyLineOptions.endCap(SquareCap())
//            blackpolyLineOptions.jointType(JointType.ROUND)
//            blackpolyLine = mMap.addPolyline(blackpolyLineOptions)
//
//            mMap.addMarker(
//                MarkerOptions().position(polylineList.get(polylineList.size - 1))
//                    .title("PickUp Location")
//            )
//
//            //Animator
//            var polyLineAnimator = ValueAnimator.ofInt(0, 100)
//            polyLineAnimator.setDuration(2000)
//            polyLineAnimator.setInterpolator(LinearInterpolator())
//            polyLineAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
//                override fun onAnimationUpdate(valueAnimator: ValueAnimator?) {
//                    var pnts: List<LatLng> = graypolyLine.points
//                    var percentValue = valueAnimator?.getAnimatedValue() as Int
//                    var size = pnts.size
//                    var new_pnts = size * (percentValue / 100.0f).toInt()
//                    var sbLst = pnts.subList(0, new_pnts)
//                    blackpolyLine.points = sbLst
//                }
//            })
//            polyLineAnimator.start()
//            carMarker = mMap.addMarker(
//                MarkerOptions().flat(true).icon(
//                    BitmapDescriptorFactory.fromResource(R.drawable.car)
//                )
//            )
//
//            handler = Handler()
//            index = -1
//            next = 1
//            handler.postDelayed(drawPathRunnable, 3000)
//
//        }
    }

    //Copied from "https://github.com/irenenaya/Kotlin-Practice/blob/master/MapsRouteActivity.kt"
    private fun decodePoly(encoded: String): ArrayList<LatLng> {
        val poly = ArrayList<LatLng>()
//        var index = 0
//        val len = encoded.length
//        var lat = 0
//        var lng = 0
//
//        while (index < len) {
//            var b: Int
//            var shift = 0
//            var result = 0
//            do {
//                b = encoded[index++].toInt() - 63
//                result = result or (b and 0x1f shl shift)
//                shift += 5
//            } while (b >= 0x20)
//            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//            lat += dlat
//
//            shift = 0
//            result = 0
//            do {
//                b = encoded[index++].toInt() - 63
//                result = result or (b and 0x1f shl shift)
//                shift += 5
//            } while (b >= 0x20)
//            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//            lng += dlng
//
//            val p = LatLng(
//                lat.toDouble() / 1E5,
//                lng.toDouble() / 1E5
//            )
//            poly.add(p)
//        }
        return poly
    }

    override fun onStart() {
        requestFineLocation()
        super.onStart()
        if (isFineLocationGranted()) {
            if (!isLocationProviderEnabled()) showGPS_NotDialog()
        } else {
            this.requestFineLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            999 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isLocationProviderEnabled()) showGPS_NotDialog()
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
            .dismiss()
    }

    private fun  isLocationProviderEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun isFineLocationGranted(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestFineLocation() {
        this.requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 999)
    }

    private fun startLocationListener() {

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
    }

    private fun displayLocation() {
        mLastLocation = fusedLocationProviderClient.lastLocation.result
        if (mLastLocation != null && location_switch.isChecked) {
            mCurrent.remove()
            mCurrent = mMap.addMarker(
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                    .position(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
                    .title("Current Location")
            )

        }// rotateMarker(mCurrent, -360, mMap)
    }

    private fun rotateMarker(mCurrent: Marker, i: Int, mMap: GoogleMap) {

//        var start = SystemClock.uptimeMillis()
//        val startRotation = mCurrent.rotation
//        val duration: Long = 1500
//        handler = Handler()
//        val interpolator = LinearInterpolator()
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                val time_elapsed = SystemClock.uptimeMillis() - start
//                var t = interpolator.getInterpolation((time_elapsed / duration).toFloat())
//                var rotation = t * i + (1 - t) * startRotation
//                mCurrent.rotation = if (-rotation > 180) rotation / 2 else rotation
//                if (t < 1.0) handler.postDelayed(this, 16)
//            }
//        }, 1000)

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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))
        mMap.isTrafficEnabled = false
        mMap.setIndoorEnabled(false)
        mMap.isBuildingsEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setMaxZoomPreference(40f)

        // Add a marker in Sydney and move the camera
        mCurrent = mMap.addMarker(MarkerOptions().position(LatLng(-34.0, 151.0)).title("Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-34.0, 151.0)))
    }
}

