package com.example.staffshelper

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.Chip

class SettingsActivity : AppCompatActivity() {
    var darkMode: Boolean = false
    var textSize: Int = 18
    var textColour: String = "none" //only get colour from darkmode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        darkMode = intent.getBooleanExtra("SETTINGS_DARKMODE", false)
        textSize = intent.getIntExtra("SETTING_TXTSIZE", 18)
        textColour = intent.getStringExtra("SETTINGS_TEXTCOLOUR").toString()


        val darkModeButton: Chip = findViewById(R.id.chip)
        darkModeButton.setOnClickListener {
            SettingsData.setSettings("settings.json", this)
            settings()
        }

        settings()

    }

    private fun settings(){
        val backGround: ConstraintLayout = findViewById(R.id.SettingsLayout)
        val darkModeTextView: TextView = findViewById(R.id.DarkModeTxt)
        val textSizeTextView: TextView = findViewById(R.id.TextSizeTxt)
        val textColourTextView: TextView = findViewById(R.id.TextColourTxt)
        if (darkMode){

            backGround.setBackgroundColor(Color.BLACK)
            darkModeTextView.setTextColor(Color.WHITE)
            textSizeTextView.setTextColor(Color.WHITE)
            textColourTextView.setTextColor(Color.WHITE)
        }
        else{
            backGround.setBackgroundColor(Color.WHITE)
            darkModeTextView.setTextColor(Color.BLACK)
            textSizeTextView.setTextColor(Color.BLACK)
            textColourTextView.setTextColor(Color.BLACK)
        }
        if (textColour != "none"){
            darkModeTextView.setTextColor(Color.parseColor(textColour))
            textSizeTextView.setTextColor(Color.parseColor(textColour))
            textColourTextView.setTextColor(Color.parseColor(textColour))
        }
        darkModeTextView.setTextSize(textSize.toFloat())
        textSizeTextView.setTextSize(textSize.toFloat())
        textColourTextView.setTextSize(textSize.toFloat())
    }
}