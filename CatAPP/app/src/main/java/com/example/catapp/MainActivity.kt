package com.example.catapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.catapp.Adapter.ItemAdapter
import com.example.catapp.data.DataSource

class MainActivity : AppCompatActivity() {
    private val facts = DataSource().loadFact()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerFacts)
        recyclerView.adapter = ItemAdapter(this, facts)

         }
}