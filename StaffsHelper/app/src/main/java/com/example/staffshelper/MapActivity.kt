package com.example.staffshelper

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.math.RoundingMode
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapActivity : AppCompatActivity(), SensorEventListener {

    private var REQUEST_LOCATION_CODE = 101

    private lateinit var fusedLocationProviderClient:
            FusedLocationProviderClient

    private  lateinit var sensorManager: SensorManager

    private var accelerometer: Sensor? = null
    var distance: Double = 0.00
    var longitudeDestination: Double = -2.175907
    var latitudeDestination: Double = 53.008578

    var updateFrames: Int = 10
    var updateFrameCount: Int = 0

    //default settings
    var darkMode: Boolean = false
    var textSize: Int = 18
    var textColour: String = "none" //only get colour from darkmode

    //on create layout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_mode)

        //get data passed from main
        darkMode = intent.getBooleanExtra("SETTINGS_DARKMODE", false)
        textSize = intent.getIntExtra("SETTING_TXTSIZE", 18)
        textColour = intent.getStringExtra("SETTINGS_TEXTCOLOUR").toString()

        //set settings
        settings()

        //gets destination text view to notify user set destination
        var destinationTextView: TextView = findViewById(R.id.Destination)

        //sets coordinates to cadman
        val cadmanCoordButton: Button = findViewById(R.id.Cadman)
        cadmanCoordButton.setOnClickListener{
            longitudeDestination = -2.18295
            latitudeDestination = 53.00980
            destinationTextView.text = "Cadman"
        }

        //sets coordinates to mellor
        val mellorCoordButton: Button = findViewById(R.id.Mellor)
        mellorCoordButton.setOnClickListener{
            longitudeDestination = -2.18053
            latitudeDestination   = 53.00964
            destinationTextView.text = "Mellor"
        }

        //sets coordinates to science building
        val scienceCoordButton: Button = findViewById(R.id.ScienceBuilding)
        scienceCoordButton.setOnClickListener{
            longitudeDestination= -2.17794
            latitudeDestination  = 53.00728
            destinationTextView.text = "Science Building"
        }

        //sets coordinates to ashley 2
        val ashley2CoordButton: Button = findViewById(R.id.Ashley2)
        ashley2CoordButton.setOnClickListener{
            longitudeDestination= -2.17333
            latitudeDestination  = 53.009792
            destinationTextView.text = "Ashley 2"
        }

        //gets accelerometer data
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        }
        else {
            //no sensor
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)


    }

    //restart sensor listener
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            accelerometer->sensorManager.registerListener(this,
        accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    //on pause stop sensor listener
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    //when accelorometer detects change in movement
    override fun onSensorChanged(event: SensorEvent) {

        //get layout text view
        val distanceTxt: TextView = findViewById(R.id.Distance)
        val speedTxt: TextView = findViewById(R.id.Speed)
        val ETATxt: TextView = findViewById(R.id.Eta)

        //get accelerometer data XYZ
        val speedX = event.values[0]
        val speedY = event.values[1]
        val speedZ = event.values[2]

        //get vector speed
        val speedMS = Math.sqrt((speedX * speedX + speedY * speedY + speedZ * speedZ).toDouble())

        //gets GPS location
        getLocation()
        if (!(getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        )
            noGPSAlert()

        //checks GPS permissions
        else if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            noPermissionsAlert()
        //permission granted get location
        else {
            getLocation()
        }

        distanceTxt.text = distance.toInt().toString() + " meters"
        speedTxt.text = speedMS.toBigDecimal().setScale(2, RoundingMode.DOWN).toString() + "m/s"
        //calculate ETA from GPS and accelerometer data
        ETATxt.text = "ETA: " + (distance / speedMS).toInt().toString() + "seconds"

    }

    //is accruacy changes
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //do nothing
    }

    //send alert if GPS is not active
    private fun noGPSAlert() {
        AlertDialog.Builder(this)
            .setTitle("Enable Location")
            .setMessage("Please enable location settings to use app")
            .setPositiveButton("Location settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()

    }

    //send alert of no permission is granted
    private fun noPermissionsAlert() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )

    }

    //gets location
    private fun getLocation() {

        //checks if permissions are granted
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        //gets location data from GPS
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location: Location? ->
            if (location == null) {
                Toast.makeText(this, "problems", Toast.LENGTH_SHORT).show()
            } else {
                //run distance calculation
                haversineDistance(
                    location.latitude.toDouble(),
                    latitudeDestination,
                    location.longitude.toDouble(),
                    longitudeDestination
                )
            }
        }

    }

    //gets permissions for location
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions,
            grantResults)
        if (requestCode == REQUEST_LOCATION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        }
    }

    //haversine used to calculate distance between 2 points on a globe
    fun haversineDistance(lat1: Double, lat2: Double, long1: Double, long2: Double) {
        var deltaLatitude = Math.toRadians(lat2 - lat1)
        var deltaLongitude = Math.toRadians(long2 - long1)

        var latitudeA = Math.toRadians(lat1)
        var latitudeB = Math.toRadians(lat2)

        var r = 6378137 //mean earth radius in meters

        var a = sin(deltaLatitude/2) * sin(deltaLatitude/2) +
                cos(latitudeA) * cos(latitudeB) *
                sin(deltaLongitude/2) * sin(deltaLongitude/2)

        var c = (2 * atan2(sqrt(a), sqrt(1-a)))
        distance = r * c


    }

    //settings for background and text
    private fun settings(){
        val backGround: ConstraintLayout = findViewById(R.id.MapLayout)
        val distanceTextView: TextView = findViewById(R.id.Distance)
        val speedTextView: TextView = findViewById(R.id.Speed)
        val ETATextView: TextView = findViewById(R.id.Eta)
        val destinationTextView: TextView = findViewById(R.id.Destination)
        if (darkMode){

            backGround.setBackgroundColor(Color.BLACK)
            distanceTextView.setTextColor(Color.WHITE)
            speedTextView.setTextColor(Color.WHITE)
            ETATextView.setTextColor(Color.WHITE)
            destinationTextView.setTextColor(Color.WHITE)
        }
        else{
            backGround.setBackgroundColor(Color.WHITE)
            distanceTextView.setTextColor(Color.BLACK)
            speedTextView.setTextColor(Color.BLACK)
            ETATextView.setTextColor(Color.BLACK)
            destinationTextView.setTextColor(Color.BLACK)
        }
        if (textColour != "none"){
            distanceTextView.setTextColor(Color.parseColor(textColour))
            speedTextView.setTextColor(Color.parseColor(textColour))
            ETATextView.setTextColor(Color.parseColor(textColour))
            destinationTextView.setTextColor(Color.parseColor(textColour))
        }
        distanceTextView.setTextSize(textSize.toFloat())
        speedTextView.setTextSize(textSize.toFloat())
        ETATextView.setTextSize(textSize.toFloat())
        destinationTextView.setTextSize(textSize.toFloat())
    }


}