package com.example.plswork

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textForename: TextView = findViewById(R.id.editTextTextPersonName)
        val startQuizButton: Button = findViewById(R.id.button)
        startQuizButton.setOnClickListener{


            val tempForename = if(textForename.text.isNullOrEmpty()) "" else textForename.text.toString()

            val quizIntent: Intent = Intent(this, Quiz::class.java).apply {
                putExtra("FORENAME", tempForename)
            }
            launcher.launch(quizIntent)
        }


    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val textScore: TextView = findViewById(R.id.textView2)
            textScore.text = it.data?.getIntExtra("RESULT", 0).toString()
        }
    }
}