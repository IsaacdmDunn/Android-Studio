package com.example.staffshelper

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData()

        val settingsButton: ImageButton = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener{
            val settingsIntent: Intent = Intent(this, SettingsActivity::class.java).apply {  }
            launcher.launch(settingsIntent)
        }

        val cameraButton: ImageButton = findViewById(R.id.cameraButton)
        cameraButton.setOnClickListener{
            val cameraIntent: Intent = Intent(this, CameraActivity::class.java).apply {  }
            launcher.launch(cameraIntent)
        }

        val mapButton: ImageButton = findViewById(R.id.mapButton)
        mapButton.setOnClickListener{
            val mapIntent: Intent = Intent(this, MapActivity::class.java).apply {  }
            launcher.launch(mapIntent)
        }

        val emergancyButton: ImageButton = findViewById(R.id.emergancyButton)
        emergancyButton.setOnClickListener{
            val emergancyIntent: Intent = Intent(this, EmergancyActivity::class.java).apply {  }
            launcher.launch(emergancyIntent)
        }


    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){}

    private fun loadData() {
        val service  = ServiceBuilder.buildService(WeatherService::class.java)
        val requestCall = service.getWeather()

        val txtName: TextView = findViewById(R.id.txtName)
        val txtTemp: TextView = findViewById(R.id.txtTemp)
        val txtDescription: TextView = findViewById(R.id.txtDescription)
        val imgIcon: ImageView = findViewById(R.id.imgIcon)

        requestCall.enqueue(object : Callback<Weather> {
            override fun onResponse(call: Call<Weather>,
                                    response: Response<Weather>
            ) {
                if (response.isSuccessful){
                    //process data
                    val weather  = response.body()!!

                    txtName.text = weather.name
                    txtTemp.text = weather.main.temp.toString()
                    txtDescription.text = weather.weather[0].description
                    Picasso.get().load("https://openweathermap.org/img/w/${weather.weather[0].icon}.png").into(imgIcon)

                }else{
                    //output alert
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("API error")
                        .setMessage("Response, but something went wrong ${response.message()}")
                        .setPositiveButton(android.R.string.ok) { _, _ -> }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()

                }
            }
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                //process failure
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("API error")
                    .setMessage("No response, and something went wrong $t")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()

            }
        })
    }

}