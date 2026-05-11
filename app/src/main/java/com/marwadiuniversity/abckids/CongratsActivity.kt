package com.marwadiuniversity.abckids

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CongratsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrats)

        val tvCongratsTitle = findViewById<TextView>(R.id.tvCongratsTitle)
        val tvCongratsMessage = findViewById<TextView>(R.id.tvCongratsMessage)
        val btnPlayAgain = findViewById<Button>(R.id.btnPlayAgain)
        val btnBackToGames = findViewById<Button>(R.id.btnBackToGames)
        val ivTrophy = findViewById<ImageView>(R.id.ivTrophy)

        // Load animations
        val bounceAnim = AnimationUtils.loadAnimation(this, android.R.anim.bounce_interpolator)
        val fadeInAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)

        tvCongratsTitle.startAnimation(fadeInAnim)
        tvCongratsMessage.startAnimation(fadeInAnim)
        ivTrophy.startAnimation(bounceAnim)

        btnPlayAgain.setOnClickListener {
            finish()
            startActivity(intent)
        }

        btnBackToGames.setOnClickListener {
            val intent = Intent(this, AnimalGamesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}