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

class MapActivity : AppCompatActivity(), SensorEventListener {

    private var REQUEST_LOCATION_CODE = 101

    private lateinit var fusedLocationProviderClient:
            FusedLocationProviderClient

    private  lateinit var sensorManager: SensorManager

    private var light: Sensor? = null
    var distance: Double = 0.00
    var latitudeDestination: Double = 2.10353
    var longitudeDestination: Double = 53.00315

    var updateFrames: Int = 10
    var updateFrameCount: Int = 0

    var darkMode: Boolean = false
    var textSize: Int = 18
    var textColour: String = "none" //only get colour from darkmode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_mode)

        darkMode = intent.getBooleanExtra("SETTINGS_DARKMODE", false)
        textSize = intent.getIntExtra("SETTING_TXTSIZE", 18)
        textColour = intent.getStringExtra("SETTINGS_TEXTCOLOUR").toString()
        settings()
        var destinationTextView: TextView = findViewById(R.id.Destination)

        val cadmanCoordButton: Button = findViewById(R.id.Cadman)
        cadmanCoordButton.setOnClickListener{
            latitudeDestination = 2.10543
            longitudeDestination = 53.00348
            destinationTextView.text = "Cadman"
        }

        val mellorCoordButton: Button = findViewById(R.id.Mellor)
        mellorCoordButton.setOnClickListener{
            latitudeDestination = 2.18053
            longitudeDestination = 53.00964
            destinationTextView.text = "Mellor"
        }

        val scienceCoordButton: Button = findViewById(R.id.ScienceBuilding)
        scienceCoordButton.setOnClickListener{
            latitudeDestination = 2.17794
            longitudeDestination = 53.00728
            destinationTextView.text = "Science Building"
        }

        val ashley2CoordButton: Button = findViewById(R.id.Ashley2)
        ashley2CoordButton.setOnClickListener{
            latitudeDestination = 2.17333
            longitudeDestination = 53.009792
            destinationTextView.text = "Ashley 2"
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            light = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        }
        else {
            //no sensor
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)


    }

    override fun onResume() {
        super.onResume()
        light?.let {
            light->sensorManager.registerListener(this,
        light, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {

        val speedXTxt: TextView = findViewById(R.id.Distance)
        val speedYTxt: TextView = findViewById(R.id.Speed)
        val speedZTxt: TextView = findViewById(R.id.Eta)

        val speedX = event.values[0]
        val speedY = event.values[1]
        val speedZ = event.values[2]

        val speedMS = Math.sqrt((speedX * speedX + speedY * speedY + speedZ * speedZ).toDouble())

        //speedXTxt.text = speedX.toString()
        //speedZTxt.text = speedZ.toString()
        getLocation()
        if (!(getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        )
            noGPSAlert()

        //Location enabled - is permission granted?
        else if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            noPermissionsAlert()
        else {
            //Location enabled and permission granted
            getLocation()
        }


        //speedXTxt.text = distance.toBigDecimal().setScale(3, RoundingMode.UP).toString() + "KM"
        //speedYTxt.text = speedMS.toBigDecimal().setScale(2, RoundingMode.UP).toString() + "m/s"

        speedZTxt.text = "ETA: " + ((distance * 1000) / speedMS).toInt().toString() + "seconds"

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //Toast.makeText(this, "Accuracy change", Toast.LENGTH_LONG).show()
    }


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

    private fun getLocation() {

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
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location: Location? ->
            if (location == null) {
                Toast.makeText(this, "problems", Toast.LENGTH_SHORT).show()
            } else {
                haversineDistance(
                    location.latitude.toDouble(),
                    latitudeDestination,
                    location.longitude.toDouble(),
                    longitudeDestination
                )
            }
        }

    }

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


    fun haversineDistance(lat1: Double, lat2: Double, long1: Double, long2: Double) {
        var deltaLatitude = Math.toRadians(lat2 - lat1)
        var deltaLongitude = Math.toRadians(long2 - long1)

        var latitudeA = Math.toRadians(lat1)//lat1 * 3.14156/180
        var latitudeB = Math.toRadians(lat2)//lat2 * 3.14156/180

        var a = (Math.sin(deltaLatitude/2) * Math.sin(deltaLatitude/2)) +
                (Math.sin(deltaLongitude/2) * Math.sin(deltaLongitude/2) * Math.cos(latitudeA) * Math.cos(latitudeB))
        var c = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)))
        distance = 6371 * c


    }

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