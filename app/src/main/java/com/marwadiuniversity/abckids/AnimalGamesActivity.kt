package com.marwadiuniversity.abckids

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.ImageView

class AnimalGamesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_games)

        setupGameCards()
        setupBackButton()
    }

    private fun setupGameCards() {
        // Animal Quiz card
        findViewById<CardView>(R.id.card_animal_quiz).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        // Phonics Matching card
        findViewById<CardView>(R.id.card_phonics_matching).setOnClickListener {
            startActivity(Intent(this, PhonicsActivity::class.java))
        }

        // Memory Cards card
        findViewById<CardView>(R.id.card_memory_cards).setOnClickListener {
            startActivity(Intent(this, CardMatchingActivity::class.java))
        }

    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }
    }
}
