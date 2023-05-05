package com.example.staffshelper

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class EmergancyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergancy)

        //gets emergency button and listens for press
        val emergencyButton: Button = findViewById(R.id.button2)
        emergencyButton.setOnClickListener(View.OnClickListener {
            //calls emergency number
            val emergencyIntent = Intent(Intent.ACTION_CALL)
            emergencyIntent.data = Uri.parse("tel: 123")
            startActivity(emergencyIntent)
        })
    }
}