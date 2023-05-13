package com.example.staffshelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsList = SettingsData.getBooks("settings.json", this)

        val setting1: TextView = findViewById(R.id.textView3)
        val setting2: TextView = findViewById(R.id.textView2)
        for (setting in settingsList){
            setting1.text = setting.darkMode.toString()
            setting2.text = setting.otherStuff.toString()
        }


    }
}