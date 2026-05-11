package com.marwadiuniversity.abckids

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.marwadiuniversity.abckids.data.GameData
import com.marwadiuniversity.abckids.data.QuizQuestion
import com.marwadiuniversity.abckids.utils.AnimationHelper
import com.marwadiuniversity.abckids.utils.SoundManager
import java.util.Locale

class GameActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var questionText: TextView
    private lateinit var animalEmoji: TextView
    private lateinit var quizScoreText: TextView
    private lateinit var quizHighScoreText: TextView
    private lateinit var quizWrongLeftText: TextView
    private lateinit var answerButtons: Array<Button>
    private lateinit var soundManager: SoundManager

    private var textToSpeech: TextToSpeech? = null
    private var isTTSReady = false
    private var isCurrentlySpeaking = false
    private var animalSoundPlayer: MediaPlayer? = null

    private var currentQuestionIndex = 0
    private var score = 0
    private var wrongAttempts = 0
    private var highScore = 0
    private val questions = ArrayList<QuizQuestion>()

    private val animalSounds = mapOf(
        "\uD83D\uDC36" to R.raw.dog_sound,
        "\uD83D\uDC31" to R.raw.cat_sound,
        "\uD83D\uDC2E" to R.raw.cow_sound,
        "\uD83D\uDC37" to R.raw.pig_sound,
        "\uD83D\uDC34" to R.raw.horse_sound,
        "\uD83D\uDC11" to R.raw.sheep_sound,
        "\uD83D\uDC38" to R.raw.frog_sound,
        "\uD83D\uDC18" to R.raw.elephant_sound,
        "\uD83E\uDD81" to R.raw.lion_sound,
        "\uD83D\uDC2F" to R.raw.tiger_sound,
        "\uD83D\uDC3B" to R.raw.bear_sound,
        "\uD83D\uDC35" to R.raw.monkey_sound,
        "\uD83D\uDC14" to R.raw.chicken_sound,
        "\uD83D\uDC26" to R.raw.bird_sound,
        "\uD83E\uDD86" to R.raw.duck_sound,
        "\uD83D\uDC3A" to R.raw.wolf_sound,
        "\uD83E\uDD85" to R.raw.eagle_sound,
        "\uD83D\uDC19" to R.raw.generic_sound,
        "\uD83D\uDC27" to R.raw.penguin_sound,
        "\uD83E\uDD89" to R.raw.owl_sound
    )

    companion object {
        private const val NEXT_QUESTION_DELAY_CORRECT = 1200L
        private const val NEXT_QUESTION_DELAY_WRONG = 1000L
        private const val ANIMAL_SOUND_DELAY = 250L
        private const val MAX_WRONG_ATTEMPTS = 10
        private const val TTS_TAG = "GameTTS"
        private const val PREFS_NAME = "animal_quiz_prefs"
        private const val PREF_HIGH_SCORE = "high_score"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initializeViews()
        initializeTextToSpeech()
        setupToolbar()
        shuffleQuestions()
        loadQuestion()
    }

    private fun initializeViews() {
        questionText = findViewById(R.id.question_text)
        animalEmoji = findViewById(R.id.animal_emoji)
        quizScoreText = findViewById(R.id.quiz_score_text)
        quizHighScoreText = findViewById(R.id.quiz_high_score_text)
        quizWrongLeftText = findViewById(R.id.quiz_wrong_left_text)
        answerButtons = arrayOf(
            findViewById(R.id.answer_btn_1),
            findViewById(R.id.answer_btn_2),
            findViewById(R.id.answer_btn_3),
            findViewById(R.id.answer_btn_4)
        )
        soundManager = SoundManager(this)
        highScore = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(PREF_HIGH_SCORE, 0)
        updateQuizStatsUI()
        setupAnswerButtons()
    }

    private fun initializeTextToSpeech() {
        try {
            textToSpeech = TextToSpeech(this, this)
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Failed to initialize TTS: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        textToSpeech?.let { tts ->
            try {
                var result = tts.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = tts.setLanguage(Locale.getDefault())
                }
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) return
                tts.setSpeechRate(0.8f)
                tts.setPitch(1.0f)
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { isCurrentlySpeaking = true }
                    override fun onDone(utteranceId: String?) { isCurrentlySpeaking = false }
                    override fun onError(utteranceId: String?) { isCurrentlySpeaking = false }
                })
                isTTSReady = true
            } catch (e: Exception) {
                Log.e(TTS_TAG, "Error configuring TTS: ${e.message}")
            }
        }
    }

    private fun setupToolbar() {
        findViewById<View>(R.id.btn_back).setOnClickListener {
            stopSpeaking()
            stopAnimalSound()
            finish()
        }
    }

    private fun setupAnswerButtons() {
        answerButtons.forEachIndexed { index, button ->
            button.setOnClickListener { checkAnswer(index) }
        }
    }

    private fun shuffleQuestions() {
        questions.clear()
        val shuffledQuestions = GameData.animalQuestions.map { originalQuestion ->
            val optionsList = originalQuestion.options.toMutableList()
            optionsList.shuffle()
            QuizQuestion(
                emoji = originalQuestion.emoji,
                correctAnswer = originalQuestion.correctAnswer,
                options = optionsList
            )
        }.toMutableList()
        shuffledQuestions.shuffle()
        questions.addAll(shuffledQuestions)
    }

    private fun loadQuestion() {
        if (wrongAttempts >= MAX_WRONG_ATTEMPTS) {
            showGameOver()
            return
        }
        if (currentQuestionIndex < questions.size) {
            displayQuestion(questions[currentQuestionIndex])
        } else {
            showFinalScore()
        }
    }

    private fun displayQuestion(question: QuizQuestion) {
        animalEmoji.text = question.emoji
        animalEmoji.startAnimation(AnimationHelper.bounceAnimation(this))
        answerButtons.forEachIndexed { index, button ->
            button.text = question.options[index]
            button.isEnabled = true
            button.visibility = View.VISIBLE
            button.background = ContextCompat.getDrawable(this, R.drawable.quiz_answer_button)
            button.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        }
    }

    private fun checkAnswer(selectedIndex: Int) {
        val currentQuestion = questions[currentQuestionIndex]
        val correctIndex = currentQuestion.options.indexOf(currentQuestion.correctAnswer)
        val isCorrect = selectedIndex == correctIndex

        answerButtons.forEach { it.isEnabled = false }
        handleAnswerResult(selectedIndex, correctIndex, isCorrect, currentQuestion.emoji)

        if (!isCorrect && wrongAttempts >= MAX_WRONG_ATTEMPTS) {
            Handler(Looper.getMainLooper()).postDelayed({ showGameOver() }, NEXT_QUESTION_DELAY_WRONG)
            return
        }

        val delay = if (isCorrect) NEXT_QUESTION_DELAY_CORRECT else NEXT_QUESTION_DELAY_WRONG
        Handler(Looper.getMainLooper()).postDelayed({
            currentQuestionIndex++
            loadQuestion()
        }, delay)
    }

    private fun handleAnswerResult(selectedIndex: Int, correctIndex: Int, isCorrect: Boolean, emoji: String) {
        if (isCorrect) {
            score++
            updateHighScoreIfNeeded()
            updateQuizStatsUI()
            showCorrectAnswer(selectedIndex)
            playSuccessSound()
            Handler(Looper.getMainLooper()).postDelayed({ playAnimalSound(emoji) }, ANIMAL_SOUND_DELAY)
        } else {
            wrongAttempts++
            score--
            updateQuizStatsUI()
            showIncorrectAnswer(selectedIndex, correctIndex)
            playErrorSound()
            val incorrectMessages = arrayOf("Try again!", "Oops! Wrong answer!", "Not quite right!", "Good try!", "Keep trying!")
            speakText(incorrectMessages.random())
        }
    }

    private fun showCorrectAnswer(index: Int) {
        answerButtons[index].background = ContextCompat.getDrawable(this, R.drawable.button_correct)
        answerButtons[index].setTextColor(Color.WHITE)
        answerButtons[index].startAnimation(AnimationHelper.correctPulseAnimation(this))
    }

    private fun showIncorrectAnswer(selected: Int, correct: Int) {
        answerButtons[selected].background = ContextCompat.getDrawable(this, R.drawable.button_incorrect)
        answerButtons[selected].setTextColor(Color.WHITE)
        answerButtons[selected].startAnimation(AnimationHelper.incorrectShakeAnimation(this))
        answerButtons[correct].background = ContextCompat.getDrawable(this, R.drawable.button_correct)
        answerButtons[correct].setTextColor(Color.WHITE)
    }

    private fun showFinalScore() {
        updateHighScoreIfNeeded()
        questionText.text = "Quiz Complete!"
        animalEmoji.text = "\uD83C\uDFC6"
        animalEmoji.startAnimation(AnimationHelper.bounceAnimation(this))
        answerButtons.forEach { it.visibility = View.GONE }

        val finalMessage = when {
            score == questions.size -> "Perfect score! Amazing work!"
            score >= questions.size * 0.8 -> "Excellent job! You did great!"
            score >= questions.size * 0.6 -> "Good work! Keep practicing!"
            else -> "Nice try! Practice makes perfect!"
        }
        Toast.makeText(this, "Final Score: $score/${questions.size}  High Score: $highScore", Toast.LENGTH_LONG).show()
        speakText(finalMessage)
    }

    private fun showGameOver() {
        updateHighScoreIfNeeded()
        questionText.text = "Game Over"
        animalEmoji.text = "\uD83D\uDE35"
        animalEmoji.startAnimation(AnimationHelper.incorrectShakeAnimation(this))
        answerButtons.forEach { it.visibility = View.GONE }
        Toast.makeText(this, "Game Over. Final Score: $score  High Score: $highScore", Toast.LENGTH_LONG).show()
        speakText("Game over. Your final score is $score")
    }

    private fun updateQuizStatsUI() {
        quizScoreText.text = "Score: $score"
        quizHighScoreText.text = "High: $highScore"
        val livesLeft = (MAX_WRONG_ATTEMPTS - wrongAttempts).coerceAtLeast(0)
        quizWrongLeftText.text = "Lives: $livesLeft"
    }

    private fun updateHighScoreIfNeeded() {
        if (score > highScore) {
            highScore = score
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putInt(PREF_HIGH_SCORE, highScore)
                .apply()
        }
    }

    private fun playAnimalSound(emoji: String) {
        try {
            val soundResourceId = animalSounds[emoji] ?: R.raw.generic_sound
            stopAnimalSound()
            animalSoundPlayer = MediaPlayer.create(this, soundResourceId)
            animalSoundPlayer?.start()
            Handler(Looper.getMainLooper()).postDelayed({ stopAnimalSound() }, 1800L)
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error playing animal sound: ${e.message}")
        }
    }

    private fun stopAnimalSound() {
        try {
            animalSoundPlayer?.let { player ->
                if (player.isPlaying) player.stop()
                player.release()
            }
            animalSoundPlayer = null
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error stopping animal sound: ${e.message}")
        }
    }

    private fun speakText(text: String) {
        if (!isTTSReady || textToSpeech == null) return
        try {
            val tts = textToSpeech ?: return
            if (isCurrentlySpeaking) tts.stop()
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "GameTTS")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "GameTTS")
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Exception while speaking: ${e.message}")
        }
    }

    private fun stopSpeaking() {
        try {
            textToSpeech?.stop()
            isCurrentlySpeaking = false
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error stopping speech: ${e.message}")
        }
    }

    private fun playSuccessSound() {
        try {
            soundManager.playSuccessSound()
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error playing success sound: ${e.message}")
        }
    }

    private fun playErrorSound() {
        try {
            soundManager.playErrorSound()
        } catch (e: Exception) {
            Log.e(TTS_TAG, "Error playing error sound: ${e.message}")
        }
    }

    override fun onPause() {
        stopSpeaking()
        stopAnimalSound()
        super.onPause()
    }

    override fun onDestroy() {
        stopSpeaking()
        stopAnimalSound()
        textToSpeech?.shutdown()
        textToSpeech = null
        isTTSReady = false
        soundManager.release()
        super.onDestroy()
    }
}
