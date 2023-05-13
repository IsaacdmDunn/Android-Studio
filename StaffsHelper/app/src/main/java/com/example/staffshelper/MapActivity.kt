package com.example.staffshelper

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.math.RoundingMode
import kotlin.math.roundToLong

class MapActivity : AppCompatActivity(), SensorEventListener {

    private var REQUEST_LOCATION_CODE = 101

    private lateinit var fusedLocationProviderClient:
            FusedLocationProviderClient

    private  lateinit var sensorManager: SensorManager
    private var light: Sensor? = null
    var distance: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_mode)


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            light = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        }
        else {
            //no sensor
        }
    }

    override fun onResume() {
        super.onResume()
        light?.let {
            light->sensorManager.registerListener(this,
        light, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {

        val speedXTxt: TextView = findViewById(R.id.SpeedX)
        val speedYTxt: TextView = findViewById(R.id.SpeedY)
        val speedZTxt: TextView = findViewById(R.id.SpeedZ)

        val speedX = event.values[0]
        val speedY = event.values[1]
        val speedZ = event.values[2]

        val speedMS = Math.sqrt((speedX * speedX + speedY * speedY + speedZ * speedZ).toDouble())

        //speedXTxt.text = speedX.toString()
        //speedZTxt.text = speedZ.toString()

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

        haversineDistance(50.0, 51.0, 50.0, 51.0)
        speedXTxt.text = distance.toBigDecimal().setScale(3, RoundingMode.UP).toString() + "KM"
        speedYTxt.text = speedMS.toBigDecimal().setScale(2, RoundingMode.UP).toString()
        speedZTxt.text = "ETA: " + (distance/speedMS).toInt().toString()
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
        val speedXTxt: TextView = findViewById(R.id.SpeedX)
        val speedYTxt: TextView = findViewById(R.id.SpeedY)

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
                speedXTxt.text = location.longitude.toString()
                speedYTxt.text = location.latitude.toString()
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


}