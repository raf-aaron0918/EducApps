package com.marwadiuniversity.abckids

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class LearningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        setupBackButton()
    }

    private fun setupBackButton() {
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}