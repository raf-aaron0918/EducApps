package com.marwadiuniversity.abckids

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marwadiuniversity.abckids.adapters.CardMatchingAdapter

class CardMatchingActivity : AppCompatActivity(), CardMatchingAdapter.OnCardInteractionListener {
    private fun drawableNameForLetter(letter: Char): String {
        val key = letter.lowercaseChar()
        return when (key) {
            'a', 'b', 'c' -> key.toString()
            else -> "letter_$key"
        }
    }

    private lateinit var tvScore: TextView
    private lateinit var tvLevel: TextView
    private lateinit var rvCards: RecyclerView
    private lateinit var btnNextLevel: Button
    private lateinit var btnRestart: Button
    private lateinit var cardMatchingAdapter: CardMatchingAdapter

    private var allCards: MutableList<MatchingCard> = mutableListOf()
    private var flippedCards: MutableList<MatchingCard> = mutableListOf()
    private var matchedPairs = 0
    private var totalPairs = 0
    private var attempts = 0
    private var currentLevel = 1
    private var isPreviewing = false
    private var totalScore = 0
    private var highScore = 0

    // MediaPlayer instances for sounds
    private var correctSoundPlayer: MediaPlayer? = null
    private var incorrectSoundPlayer: MediaPlayer? = null
    private var levelCompleteSoundPlayer: MediaPlayer? = null
    private var gameCompleteSoundPlayer: MediaPlayer? = null

    private val totalLevels = 8
    private val fixedCols = 3
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "memory_game_prefs"
        private const val KEY_HIGH_SCORE = "high_score"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_matching)

        setupViews()
        setupScoreStorage()
        initializeSounds()
        startLevel(currentLevel)
    }

    private fun setupScoreStorage() {
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        highScore = prefs.getInt(KEY_HIGH_SCORE, 0)
    }

    private fun setupViews() {
        tvScore = findViewById(R.id.tvScore)
        tvLevel = findViewById(R.id.tvLevel)
        rvCards = findViewById(R.id.rvCards)
        btnNextLevel = findViewById(R.id.btnNextLevel)
        btnRestart = findViewById(R.id.btnRestart)

        // Add back button functionality
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // or onBackPressed() if you want to use the default back behavior
        }

        btnNextLevel.setOnClickListener {
            if (currentLevel < totalLevels) {
                currentLevel++
                startLevel(currentLevel)
            } else {
                showGameCompleted()
            }
        }

        btnRestart.setOnClickListener {
            totalScore = 0
            startLevel(currentLevel)
        }

        updateScore()
    }

    private fun initializeSounds() {
        try {
            correctSoundPlayer = MediaPlayer.create(this, R.raw.correct_sound)
            incorrectSoundPlayer = MediaPlayer.create(this, R.raw.incorrect_sound)
            levelCompleteSoundPlayer = MediaPlayer.create(this, R.raw.level_complete_sound)
            gameCompleteSoundPlayer = MediaPlayer.create(this, R.raw.game_complete_sound)
        } catch (e: Exception) {
            // Sounds not available, continue without them
        }
    }

    private fun startLevel(level: Int) {
        val config = buildLevelConfig(level)

        // Reset game state
        matchedPairs = 0
        attempts = 0
        flippedCards.clear()
        isPreviewing = true

        // Setup grid layout - Always 3 columns
        rvCards.layoutManager = GridLayoutManager(this, fixedCols)

        // Generate cards for this level
        generateCardsForLevel(config)

        // Setup adapter
        cardMatchingAdapter = CardMatchingAdapter(allCards, this, currentLevel)
        rvCards.adapter = cardMatchingAdapter

        // Hide next level button until level is completed
        btnNextLevel.visibility = View.GONE

        updateScore()
        updateLevelDisplay()
        showAllCardsThenHide()
    }

    private fun buildLevelConfig(level: Int): LevelConfig {
        // Level 1 = 4 cards, then +2 cards every level
        val cardsCount = 4 + ((level - 1) * 2)
        val pairs = cardsCount / 2
        val rows = (cardsCount + fixedCols - 1) / fixedCols
        return LevelConfig(rows = rows, cols = fixedCols, pairs = pairs)
    }

    private fun generateCardsForLevel(config: LevelConfig) {
        allCards.clear()
        totalPairs = config.pairs

        // Create pairs using letters a-z with random selection
        val allLetters = ('a'..'z').toList()
        val selectedLetters = allLetters.shuffled().take(config.pairs)
        var cardId = 1

        for ((index, letter) in selectedLetters.withIndex()) {
            val pairId = index + 1
            val drawableName = drawableNameForLetter(letter)

            // Create pair of cards
            allCards.add(
                MatchingCard(
                    id = cardId++,
                    pairId = pairId,
                    imageResource = drawableName,
                    description = "Letter $letter"
                )
            )

            allCards.add(
                MatchingCard(
                    id = cardId++,
                    pairId = pairId,
                    imageResource = drawableName,
                    description = "Letter $letter"
                )
            )
        }

        // Fill remaining slots with empty cards if needed
        val totalSlots = config.rows * config.cols
        while (allCards.size < totalSlots) {
            allCards.add(
                MatchingCard(
                    id = cardId++,
                    pairId = -1, // Special ID for empty cards
                    imageResource = "empty",
                    description = "Empty slot",
                    isEmpty = true
                )
            )
        }

        // Shuffle all cards multiple times for better randomness
        allCards.shuffle()
        allCards.shuffle()
        allCards.shuffle()
    }

    override fun onCardClicked(card: MatchingCard, position: Int) {
        if (isPreviewing || card.isFlipped || card.isMatched || card.isEmpty || flippedCards.size >= 2) {
            return // Ignore if card already flipped, matched, empty, if 2 cards are already flipped, or during preview
        }

        // Flip the card
        card.isFlipped = true
        flippedCards.add(card)
        cardMatchingAdapter.notifyItemChanged(position)

        // Animate card flip
        animateCardFlip(position)

        if (flippedCards.size == 2) {
            attempts++
            checkForMatch()
        }
    }

    private fun checkForMatch() {
        Handler(Looper.getMainLooper()).postDelayed({
            val card1 = flippedCards[0]
            val card2 = flippedCards[1]

            if (card1.pairId == card2.pairId && card1.pairId != -1) {
                // Match found!
                card1.isMatched = true
                card2.isMatched = true
                matchedPairs++

                playCorrectSound()

                if (matchedPairs == totalPairs) {
                    showLevelCompleted()
                }
            } else {
                // No match - flip cards back
                card1.isFlipped = false
                card2.isFlipped = false
                playIncorrectSound()
            }

            flippedCards.clear()
            cardMatchingAdapter.notifyDataSetChanged()
            updateScore()
        }, 1000) // Delay to show both cards before checking
    }

    private fun animateCardFlip(position: Int) {
        try {
            val viewHolder = rvCards.findViewHolderForAdapterPosition(position)
            viewHolder?.itemView?.let { cardView ->
                val flipAnimation = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 180f, 0f)
                flipAnimation.duration = 600
                flipAnimation.start()
            }
        } catch (e: Exception) {
            // Animation failed, continue without it
        }
    }

    private fun updateScore() {
        tvScore.text = "Pairs: $matchedPairs/$totalPairs | Tries: $attempts | Score: $totalScore | Best: $highScore"
    }

    private fun updateLevelDisplay() {
        tvLevel.text = "Level $currentLevel/$totalLevels"
    }

    private fun showAllCardsThenHide() {
        // Open all cards at level start so kids can memorize them first.
        allCards.forEach { card ->
            if (!card.isEmpty) {
                card.isFlipped = true
            }
        }
        cardMatchingAdapter.notifyDataSetChanged()

        Handler(Looper.getMainLooper()).postDelayed({
            allCards.forEach { card ->
                if (!card.isMatched && !card.isEmpty) {
                    card.isFlipped = false
                }
            }
            isPreviewing = false
            cardMatchingAdapter.notifyDataSetChanged()
        }, 1800)
    }

    private fun playCorrectSound() {
        try {
            correctSoundPlayer?.start()
        } catch (e: Exception) {
            // Sound playback failed, continue silently
        }
    }

    private fun playIncorrectSound() {
        try {
            incorrectSoundPlayer?.start()
        } catch (e: Exception) {
            // Sound playback failed, continue silently
        }
    }

    private fun playLevelCompleteSound() {
        try {
            levelCompleteSoundPlayer?.start()
        } catch (e: Exception) {
            // Sound playback failed, continue silently
        }
    }

    private fun playGameCompleteSound() {
        try {
            gameCompleteSoundPlayer?.start()
        } catch (e: Exception) {
            // Sound playback failed, continue silently
        }
    }

    private fun showLevelCompleted() {
        val levelScore = calculateLevelScore()
        totalScore += levelScore
        updateHighScoreIfNeeded()

        val successRate = if (attempts > 0) {
            ((totalPairs.toDouble() / attempts) * 100).toInt()
        } else 100

        if (currentLevel < totalLevels) {
            playLevelCompleteSound()
            Toast.makeText(
                this,
                "Level $currentLevel Complete! +$levelScore points | Success: $successRate%",
                Toast.LENGTH_LONG
            ).show()
            btnNextLevel.visibility = View.VISIBLE
        } else {
            showGameCompleted()
        }
        updateScore()
    }

    private fun showGameCompleted() {
        playGameCompleteSound()
        Toast.makeText(
            this,
            "Congratulations! You completed all levels! Score: $totalScore | Best: $highScore",
            Toast.LENGTH_LONG
        ).show()

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 3000)
    }

    private fun calculateLevelScore(): Int {
        val pairPoints = 10 * currentLevel
        val levelBonus = currentLevel * 20
        val attemptPenalty = maxOf(0, attempts - totalPairs) * 2
        return (totalPairs * pairPoints) + levelBonus - attemptPenalty
    }

    private fun updateHighScoreIfNeeded() {
        if (totalScore > highScore) {
            highScore = totalScore
            prefs.edit().putInt(KEY_HIGH_SCORE, highScore).apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        correctSoundPlayer?.release()
        incorrectSoundPlayer?.release()
        levelCompleteSoundPlayer?.release()
        gameCompleteSoundPlayer?.release()
    }

    data class LevelConfig(
        val rows: Int,
        val cols: Int,
        val pairs: Int
    )
}
