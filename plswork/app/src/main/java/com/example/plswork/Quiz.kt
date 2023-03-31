package com.example.plswork

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Quiz : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        val greetingText: TextView = findViewById(R.id.greeting)

        val score: Int = 69

        val greetingMessage = getString(R.string.bruhkys, "${intent?.extras?.getString("FORENAME")}")
        greetingText.text = greetingMessage

        val startQuizButton: Button = findViewById(R.id.button2)
        startQuizButton.setOnClickListener{
            val responceIntent: Intent = Intent().apply {
                putExtra("RESULT", score)
            }
            setResult(Activity.RESULT_OK, responceIntent)
            finish()
        }
    }
}