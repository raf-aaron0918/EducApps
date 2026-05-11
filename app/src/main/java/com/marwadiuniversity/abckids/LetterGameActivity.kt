package com.marwadiuniversity.abckids

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Collections

class LetterGameActivity : AppCompatActivity() {

    // Letter pool: A through G (7 unique letters)
    private val letterPool = listOf("A", "B", "C", "D", "E", "F", "G")

    // Balloon drawable resources mapped to letters
    private val balloonDrawables = listOf(
        R.drawable.balloon_red,
        R.drawable.balloon_blue,
        R.drawable.balloon_green,
        R.drawable.balloon_yellow,
        R.drawable.balloon_purple,
        R.drawable.balloon_orange,
        R.drawable.balloon_pink
    )

    private lateinit var gridLetters: GridLayout
    private lateinit var tvScore: TextView
    private lateinit var tvCollected: TextView
    private lateinit var tvMessage: TextView

    private var collectedLetters = mutableSetOf<String>()
    private var letterItems = mutableListOf<LetterItem>()

    private var mediaPlayer: MediaPlayer? = null

    private val balloonViews = mutableListOf<View>()

    companion object {
        private const val TOTAL_LETTERS = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_game)

        initializeViews()
        setupBackButton()
        generateLetters()
        buildGrid()
    }

    private fun initializeViews() {
        gridLetters = findViewById(R.id.gridLetters)
        tvScore = findViewById(R.id.tvScore)
        tvCollected = findViewById(R.id.tvCollected)
        tvMessage = findViewById(R.id.tvMessage)
    }

    private fun setupBackButton() {
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun generateLetters() {
        val shuffled = letterPool.shuffled()
        letterItems.clear()
        collectedLetters.clear()

        for (i in shuffled.indices) {
            letterItems.add(
                LetterItem(
                    letter = shuffled[i],
                    balloonResId = balloonDrawables[i],
                    collected = false
                )
            )
        }
        Collections.shuffle(letterItems)
    }

    private fun buildGrid() {
        gridLetters.removeAllViews()
        balloonViews.clear()

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(8, 8, 8, 8)
        }

        for (i in letterItems.indices) {
            val item = letterItems[i]

            // Inflate balloon item
            val view = layoutInflater.inflate(R.layout.item_letter_balloon, gridLetters, false)

            val balloonBg = view.findViewById<ImageView>(R.id.balloon_background)
            val tvLetter = view.findViewById<TextView>(R.id.tv_letter)

            balloonBg.setImageResource(item.balloonResId)
            tvLetter.text = item.letter

            // Set click listener
            view.setOnClickListener {
                onLetterClicked(item, i, view, balloonBg, tvLetter)
            }

            gridLetters.addView(view, params)
            balloonViews.add(view)
        }

        updateUI()
    }

    private fun onLetterClicked(item: LetterItem, index: Int, view: View, balloonBg: ImageView, tvLetter: TextView) {
        if (item.collected) {
            Toast.makeText(this, "${item.letter} already collected!", Toast.LENGTH_SHORT).show()
            return
        }

        // Collect the letter
        item.collected = true
        collectedLetters.add(item.letter)

        // Play sound if available
        try {
            val resId = resources.getIdentifier("collect_sound", "raw", packageName)
            if (resId != 0) {
                mediaPlayer?.release()
                mediaPlayer = null
                mediaPlayer = MediaPlayer.create(this, resId)
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Animate the balloon
        val bounceAnim = AnimationUtils.loadAnimation(this, android.R.anim.bounce_interpolator)
        bounceAnim.duration = 500
        view.startAnimation(bounceAnim)

        // Change background to green to indicate collected
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#4CAF50"))
            setStroke(3, Color.parseColor("#1B5E20"))
        }
        balloonBg.background = bg
        balloonBg.setImageResource(0)
        tvLetter.alpha = 0.7f

        updateUI()

        // Check for completion
        if (collectedLetters.size >= TOTAL_LETTERS) {
            Handler(Looper.getMainLooper()).postDelayed({
                showCongratulations()
            }, 800)
        }
    }

    private fun updateUI() {
        tvScore.text = "Score: ${collectedLetters.size}/$TOTAL_LETTERS"
        tvCollected.text = "Collected: ${collectedLetters.joinToString(", ")}"

        val remaining = TOTAL_LETTERS - collectedLetters.size
        tvMessage.text = if (remaining > 0) {
            "Find the remaining $remaining letter${if (remaining > 1) "s" else ""}!"
        } else {
            "All letters collected!"
        }
    }

    private fun showCongratulations() {
        try {
            // Play completion sound if available
            try {
                val resId = resources.getIdentifier("complete_sound", "raw", packageName)
                if (resId != 0) {
                    mediaPlayer?.release()
                    mediaPlayer = null
                    mediaPlayer = MediaPlayer.create(this, resId)
                    mediaPlayer?.start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent = Intent(this, CongratsActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Congratulations! All letters collected!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    data class LetterItem(
        val letter: String,
        val balloonResId: Int,
        var collected: Boolean
    )
}