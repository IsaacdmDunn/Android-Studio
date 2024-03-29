package com.example.staffshelper

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout

class EmergancyActivity : AppCompatActivity() {

    //default settings
    var darkMode: Boolean = false
    var textSize: Int = 18
    var textColour: String = "none" //only get colour from darkmode

    //on create layout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergancy)

        //get data passed from main
        darkMode = intent.getBooleanExtra("SETTINGS_DARKMODE", false)
        textSize = intent.getIntExtra("SETTING_TXTSIZE", 18)
        textColour = intent.getStringExtra("SETTINGS_TEXTCOLOUR").toString()

        //sets settings
        settings()

        //gets emergency button and listens for press
        val emergencyButton: Button = findViewById(R.id.EmergancyButton)
        emergencyButton.setOnClickListener(View.OnClickListener {
            //calls emergency number
            val emergencyIntent = Intent(Intent.ACTION_CALL)
            emergencyIntent.data = Uri.parse("tel: 123")
            startActivity(emergencyIntent)
        })


    }

    //settings for background and text
    private fun settings(){
        val backGround: ConstraintLayout = findViewById(R.id.EmergancyLayout)
        if (darkMode){

            backGround.setBackgroundColor(Color.BLACK)
        }
        else{
            backGround.setBackgroundColor(Color.WHITE)
        }
    }
}