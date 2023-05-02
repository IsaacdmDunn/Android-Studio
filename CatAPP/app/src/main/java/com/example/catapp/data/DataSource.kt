package com.example.catapp.data

import com.example.catapp.R
import com.example.catapp.model.Fact

class DataSource {
    fun loadFact(): List<Fact>{
        return listOf<Fact>(
            Fact(R.string.fact1, R.drawable.img2),
            Fact(R.string.fact2, R.drawable.img2),
            Fact(R.string.fact3, R.drawable.img3),
            Fact(R.string.fact3, R.drawable.img3),
            Fact(R.string.fact3, R.drawable.img3),
            Fact(R.string.fact3, R.drawable.img3),
            Fact(R.string.fact3, R.drawable.img3),
            Fact(R.string.fact3, R.drawable.img3),
            Fact(R.string.fact3, R.drawable.img3)
        )
    }
}