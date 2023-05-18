package com.example.staffshelper

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration

class MainActivity : AppCompatActivity() {
    //on create layout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //gets and sets settings
        val settingsList = SettingsData.getSettings("settings.json", this)
        settings(settingsList)

        //gets weather
        getWeather()

        //launches settings activity and passes setting data
        val settingsButton: ImageButton = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener{
            val settingsIntent: Intent = Intent(this, SettingsActivity::class.java).apply {
                putExtra("SETTINGS_DARKMODE", settingsList[0].darkMode)
                putExtra("SETTINGS_TEXTSIZE", settingsList[0].textSize)
                putExtra("SETTINGS_TEXTCOLOUR", settingsList[0].txtColour)
            }
            launcher.launch(settingsIntent)
        }

        //launches camera activity and passes setting data
        val cameraButton: ImageButton = findViewById(R.id.cameraButton)
        cameraButton.setOnClickListener{
            val cameraIntent: Intent = Intent(this, CameraActivity::class.java).apply {
                putExtra("SETTINGS_DARKMODE", settingsList[0].darkMode)
                putExtra("SETTINGS_TEXTSIZE", settingsList[0].textSize)
                putExtra("SETTINGS_TEXTCOLOUR", settingsList[0].txtColour)
            }
            launcher.launch(cameraIntent)
        }

        //launches map activity and passes setting data
        val mapButton: ImageButton = findViewById(R.id.mapButton)
        mapButton.setOnClickListener{
            val mapIntent: Intent = Intent(this, MapActivity::class.java).apply {
                putExtra("SETTINGS_DARKMODE", settingsList[0].darkMode)
                putExtra("SETTINGS_TEXTSIZE", settingsList[0].textSize)
                putExtra("SETTINGS_TEXTCOLOUR", settingsList[0].txtColour)
            }
            launcher.launch(mapIntent)
        }

        //launches emergency activity and passes setting data
        val emergancyButton: ImageButton = findViewById(R.id.emergancyButton)
        emergancyButton.setOnClickListener{
            val emergancyIntent: Intent = Intent(this, EmergancyActivity::class.java).apply {
                putExtra("SETTINGS_DARKMODE", settingsList[0].darkMode)
                putExtra("SETTINGS_TEXTSIZE", settingsList[0].textSize)
                putExtra("SETTINGS_TEXTCOLOUR", settingsList[0].txtColour)
            }
            launcher.launch(emergancyIntent)
        }


    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
    }

    //settings for background and text
    private fun settings(settingsList: ArrayList<SettingsData>){
        //elements
        val txtName: TextView = findViewById(R.id.txtName)
        val txtWeatherWarning: TextView = findViewById(R.id.txtWeatherWarning)
        val txtTemp: TextView = findViewById(R.id.txtTemp)
        val txtDescription: TextView = findViewById(R.id.txtDescription)
        val imgIcon: ImageView = findViewById(R.id.imgIcon)

        val backGround: ConstraintLayout = findViewById(R.id.MainLayout)
        if (settingsList[0].darkMode){

            backGround.setBackgroundColor(Color.BLACK)
            txtName.setTextColor(Color.WHITE)
            txtWeatherWarning.setTextColor(Color.WHITE)
            txtTemp.setTextColor(Color.WHITE)
            txtDescription.setTextColor(Color.WHITE)
        }
        else{
            backGround.setBackgroundColor(Color.WHITE)
            txtName.setTextColor(Color.BLACK)
            txtWeatherWarning.setTextColor(Color.BLACK)
            txtTemp.setTextColor(Color.BLACK)
            txtDescription.setTextColor(Color.BLACK)
        }
        if (settingsList[0].txtColour != "none"){
            txtName.setTextColor(Color.parseColor(settingsList[0].txtColour))
            txtWeatherWarning.setTextColor(Color.parseColor(settingsList[0].txtColour))
            txtTemp.setTextColor(Color.parseColor(settingsList[0].txtColour))
            txtDescription.setTextColor(Color.parseColor(settingsList[0].txtColour))
        }
        txtName.setTextSize(settingsList[0].textSize.toFloat())
        txtWeatherWarning.setTextSize(settingsList[0].textSize.toFloat())
        txtTemp.setTextSize(settingsList[0].textSize.toFloat())
        txtDescription.setTextSize(settingsList[0].textSize.toFloat())
    }

    //gets weather
    private fun getWeather() {
        //gets weather data from api
        val service  = ServiceBuilder.buildService(WeatherService::class.java)
        val requestCall = service.getWeather()

        //gets layout text/ image
        val txtName: TextView = findViewById(R.id.txtName)
        val txtWeatherWarning: TextView = findViewById(R.id.txtWeatherWarning)
        val txtTemp: TextView = findViewById(R.id.txtTemp)
        val txtDescription: TextView = findViewById(R.id.txtDescription)
        val imgIcon: ImageView = findViewById(R.id.imgIcon)

        //sets weather api data as object
        requestCall.enqueue(object : Callback<Weather> {
            override fun onResponse(call: Call<Weather>,
                                    response: Response<Weather>
            ) {
                if (response.isSuccessful){
                    //set data as text/ image content
                    val weather  = response.body()!!

                    txtName.text = weather.name
                    if(weather.weather[0].description == "rain")
                    {
                        txtWeatherWarning.text = "!"
                    }
                    txtTemp.text = weather.main.temp.toString()
                    txtDescription.text = weather.weather[0].description
                    Picasso.get().load("https://openweathermap.org/img/w/${weather.weather[0].icon}.png").into(imgIcon)

                }else{
                    //output alert
                    Toast.makeText(baseContext, "Error getting weather", Toast.LENGTH_SHORT).show()


                }
            }
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                //process failure
                Toast.makeText(baseContext, "Failed to get weather", Toast.LENGTH_SHORT).show()

            }
        })
    }

}