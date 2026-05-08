package com.marwadiuniversity.abckids

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class LearningOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_options)

        setupBackButton()
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }
    }
}