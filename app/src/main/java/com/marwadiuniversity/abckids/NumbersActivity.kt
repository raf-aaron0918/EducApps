package com.marwadiuniversity.abckids

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.core.view.GestureDetectorCompat
import com.marwadiuniversity.abckids.data.NumberData
import com.marwadiuniversity.abckids.data.NumberItem
import java.util.*
import kotlin.math.abs

class NumbersActivity : AppCompatActivity(), TextToSpeech.OnInitListener, GestureDetector.OnGestureListener {

    private lateinit var btnBack: ImageView
    private lateinit var numberCard: CardView
    private lateinit var numberText: TextView
    private lateinit var numberImage: ImageView
    private lateinit var wordText: TextView
    private lateinit var progressText: TextView
    private lateinit var backgroundLayout: View

    private lateinit var gestureDetector: GestureDetectorCompat

    private var tts: TextToSpeech? = null
    private var isTTSReady = false
    private var isCurrentlySpeaking = false
    private var shouldAutoSpeakOnInit = true
    private var currentIndex = 0
    private var isTransitioning = false
    private var pendingSpeechAfterTransition = false
    private val mainHandler = Handler(Looper.getMainLooper())

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()
        setContentView(R.layout.activity_numbers)

        initViews()
        initGestureDetector()
        initTextToSpeech()
        displayCurrentNumber()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        numberCard = findViewById(R.id.number_card_view)
        numberText = findViewById(R.id.number_text)
        numberImage = findViewById(R.id.number_image)
        wordText = findViewById(R.id.number_word)
        progressText = findViewById(R.id.progress_text)
        backgroundLayout = findViewById(R.id.main_content_area)

        setupClickListeners()
    }

    private fun initGestureDetector() {
        gestureDetector = GestureDetectorCompat(this, this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        numberCard.setOnClickListener { handleCardInteraction() }

        numberCard.setOnTouchListener { _, event ->
            val gestureResult = gestureDetector.onTouchEvent(event)
            if (gestureResult) {
                true 
            } else {
                false 
            }
        }

        backgroundLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true 
        }
    }

    override fun onDown(e: MotionEvent): Boolean = true
    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean = false
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent) { handleCardInteraction() }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (e1 == null || isTransitioning) return false
        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y
        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0) {
                    navigateToPrevious()
                    return true
                } else {
                    navigateToNext()
                    return true
                }
            }
        }
        return false
    }

    private fun navigateToPrevious() {
        if (currentIndex > 0 && !isTransitioning) {
            isTransitioning = true
            pendingSpeechAfterTransition = true
            stopAllSpeech()
            currentIndex--
            animateTransition {
                displayCurrentNumber()
            }
        }
    }

    private fun navigateToNext() {
        if (currentIndex < NumberData.numberList.size - 1 && !isTransitioning) {
            isTransitioning = true
            pendingSpeechAfterTransition = true
            stopAllSpeech()
            currentIndex++
            animateTransition {
                displayCurrentNumber()
            }
        }
    }

    private fun animateTransition(onComplete: () -> Unit) {
        numberCard.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(100) // Faster transition animation
            .withEndAction {
                onComplete()
            }
            .start()
    }

    private fun handleCardInteraction() {
        if (isCurrentlySpeaking || isTransitioning) return

        if (!isTTSReady) {
            Toast.makeText(this, "Voice initializing...", Toast.LENGTH_SHORT).show()
            return
        }

        val currentItem = NumberData.numberList[currentIndex]
        speakNumber(currentItem)

        numberCard.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(100)
            .withEndAction {
                numberCard.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun initTextToSpeech() {
        tts = TextToSpeech(this, this)
    }

override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { tts ->
                // Set natural human-like voice parameters
                try {
                    val bestVoice = tts.voices.filter { v ->
                        v.locale.language == Locale.US.language && !v.isNetworkConnectionRequired
                    }.maxByOrNull { v -> v.quality }
                    bestVoice?.let { v -> tts.voice = v }
                } catch (e: Exception) {
                    tts.setLanguage(Locale.US)
                }

                tts.setSpeechRate(0.9f)
                tts.setPitch(1.0f)

                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { isCurrentlySpeaking = true }
                    override fun onDone(utteranceId: String?) { isCurrentlySpeaking = false }
                    override fun onError(utteranceId: String?) { isCurrentlySpeaking = false }
                })
                isTTSReady = true

if (shouldAutoSpeakOnInit) {
                     shouldAutoSpeakOnInit = false
                     mainHandler.postDelayed({
                         if (isTTSReady && !isDestroyed && !isFinishing) {
                             speakNumber(NumberData.numberList[currentIndex])
                         }
                     }, 500)
                 }
            }
        }
}

    private fun displayCurrentNumber() {
        val currentItem = NumberData.numberList[currentIndex]
        progressText.text = "Number ${currentIndex + 1} of ${NumberData.numberList.size}"

        numberText.text = currentItem.number
        numberImage.setImageResource(currentItem.fingerImageRes)
        wordText.text = currentItem.word

        // Set opacity to 204 to match Alphabet activity
        val startColor = ColorUtils.setAlphaComponent(Color.parseColor(currentItem.colorStart), 204)
        val endColor = ColorUtils.setAlphaComponent(Color.parseColor(currentItem.colorEnd), 204)
        
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(startColor, endColor)
        ).apply {
            cornerRadius = 40f * resources.displayMetrics.density
            setStroke(2, Color.parseColor("#80FFFFFF"))
        }
        
        numberCard.background = gradientDrawable
        numberCard.cardElevation = 12f * resources.displayMetrics.density

        numberCard.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300) // Faster animation for quicker speech
            .withEndAction {
                // Mark transition as complete and handle pending speech
                isTransitioning = false

                if (pendingSpeechAfterTransition && isTTSReady) {
                    pendingSpeechAfterTransition = false
                    // Quick speech after swipe transitions
                    mainHandler.postDelayed({
                        if (isTTSReady && !isCurrentlySpeaking && !isTransitioning && !isDestroyed && !isFinishing) {
                            speakNumber(currentItem)
                        }
                    }, 50) // Reduced delay for faster response
                }
            }
            .start()
    }

    private fun speakNumber(item: NumberItem) {
        if (!isTTSReady || isDestroyed || isFinishing) return

        // Stop any current speech
        tts?.stop()
        isCurrentlySpeaking = false

        val textToSpeak = item.number

        // Minimal delay for immediate speech response
        mainHandler.postDelayed({
            if (isTTSReady && tts != null && !isDestroyed && !isFinishing) {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        val params = Bundle()
                        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "NumberSpeak_${System.currentTimeMillis()}")
                        val result = tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "NumberSpeak_${System.currentTimeMillis()}")
                        if (result == TextToSpeech.ERROR) {
                            isCurrentlySpeaking = false
                        }
                    } else {
                        val params = HashMap<String, String>()
                        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "NumberSpeak_${System.currentTimeMillis()}"
                        @Suppress("DEPRECATION")
                        val result = tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params)
                        if (result == TextToSpeech.ERROR) {
                            isCurrentlySpeaking = false
                        }
                    }
                } catch (e: Exception) {
                    isCurrentlySpeaking = false
                    e.printStackTrace()
                }
            }
        }, 30) // Very short delay for faster speech response
    }

    private fun stopAllSpeech() {
        tts?.stop()
        isCurrentlySpeaking = false
    }

    override fun onDestroy() {
        stopAllSpeech()
        mainHandler.removeCallbacksAndMessages(null)
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        stopAllSpeech()
        pendingSpeechAfterTransition = false
        mainHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        stopAllSpeech()
        isTransitioning = false
        pendingSpeechAfterTransition = false
    }
}