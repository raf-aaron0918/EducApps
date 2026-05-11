package com.marwadiuniversity.abckids

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.sin

class MusicSelectionActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var instrumentContainer: RelativeLayout

    private val instruments = listOf(
        InstrumentData(R.drawable.piano_icon, "Piano", "#4FC3F7", "#0288D1", PianoActivity::class.java),
        InstrumentData(R.drawable.flute_icon, "Flute", "#81C784", "#388E3C", FluteActivity::class.java),
        InstrumentData(R.drawable.harmonica_icon, "Harmonica", "#FF8A80", "#FF5252", HarmonicaActivity::class.java)
    )

    data class InstrumentData(
        val iconResId: Int,
        val name: String,
        val primaryColor: String,
        val secondaryColor: String,
        val activityClass: Class<*>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createMainLayout())

        setupBackButton()
        createCircularInstrumentLayout()
        animateInstrumentsEntrance()
    }

    private fun createMainLayout(): View {
        val mainLayout = RelativeLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundResource(R.drawable.background)
        }

        addFloatingInstrumentEmojis(mainLayout)

        val headerLayout = createHeader()
        mainLayout.addView(headerLayout)

        instrumentContainer = RelativeLayout(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            ).apply {
                addRule(RelativeLayout.BELOW, headerLayout.id)
                setMargins(10, -20, 10, 50)
            }
        }
        mainLayout.addView(instrumentContainer)

        return mainLayout
    }

    private fun createHeader(): LinearLayout {
        return LinearLayout(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(14, 12, 14, 6)
            gravity = android.view.Gravity.CENTER_VERTICAL
            background = resources.getDrawable(android.R.color.transparent, null)

            backButton = ImageView(this@MusicSelectionActivity).apply {
                layoutParams = LinearLayout.LayoutParams(44, 44).apply {
                    setMargins(0, 0, 12, 0)
                }
                setImageResource(R.drawable.ic_back)
                setPadding(10, 10, 10, 10)
                setBackgroundResource(R.drawable.quiz_surface_bg)
                elevation = 4f
                contentDescription = "Back"
            }
            addView(backButton)

            val titleImage = ImageView(this@MusicSelectionActivity).apply {
                layoutParams = LinearLayout.LayoutParams(0, 190, 1f).apply {
                    topMargin = 20
                }
                setImageResource(R.drawable.instrument_content)
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = "Instrument"
            }
            addView(titleImage)
        }
    }

    private fun createCircularInstrumentLayout() {
        try {
            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels

            val headerHeight = 150
            val availableHeight = screenHeight - headerHeight

            val centerX = screenWidth / 2
            val centerY = headerHeight + (availableHeight / 2)

            val maxRadius = Math.min(
                (screenWidth - 120) / 2,
                (availableHeight - 80) / 2
            ).toFloat()
            val radius = maxRadius * 0.95f

            val buttonSize = Math.min(screenWidth / 2.5f, 380f).toInt()
            val angleStep = 360f / instruments.size

            instruments.forEachIndexed { index, instrument ->
                val angle = Math.toRadians((index * angleStep - 90).toDouble())
                val x = (centerX + radius * cos(angle)).toInt() - buttonSize / 2
                val y = (centerY + radius * sin(angle)).toInt() - buttonSize / 2

                val instrumentButton = createLargeInstrumentButton(instrument, buttonSize)

                val safeMarginX = 15
                val safeMarginY = 15
                val params = RelativeLayout.LayoutParams(buttonSize, buttonSize).apply {
                    leftMargin = Math.max(safeMarginX, Math.min(x, screenWidth - buttonSize - safeMarginX))
                    topMargin = Math.max(safeMarginY, Math.min(y, screenHeight - buttonSize - safeMarginY))
                }
                instrumentButton.layoutParams = params

                instrumentButton.alpha = 0f
                instrumentButton.scaleX = 0f
                instrumentButton.scaleY = 0f

                instrumentContainer.addView(instrumentButton)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createLargeInstrumentButton(instrument: InstrumentData, size: Int): Button {
        return Button(this).apply {
            text = instrument.name
            textSize = 17f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            gravity = android.view.Gravity.CENTER_HORIZONTAL or android.view.Gravity.CENTER_VERTICAL
            background = createEnhancedCircularBackground(instrument.primaryColor, instrument.secondaryColor)
            elevation = 28f
            setShadowLayer(16f, 8f, 8f, Color.parseColor("#60000000"))
            setPadding(20, 24, 20, 28)
            maxLines = 1
            layoutParams = RelativeLayout.LayoutParams(size, size)
            val icon: Drawable? = resources.getDrawable(instrument.iconResId, null)
            icon?.setBounds(0, 0, 220, 220)
            setCompoundDrawables(null, icon, null, null)
            compoundDrawablePadding = 10
            startPulsingAnimation()

            setOnClickListener {
                try {
                    animateButtonPress(this) {
                        val intent = Intent(this@MusicSelectionActivity, instrument.activityClass)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun Button.startPulsingAnimation() {
        try {
            val pulseAnimator = ValueAnimator.ofFloat(1f, 1.08f).apply {
                duration = 2200
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                addUpdateListener { animation ->
                    val scale = animation.animatedValue as Float
                    scaleX = scale
                    scaleY = scale
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({ pulseAnimator.start() }, (0..1200).random().toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun animateInstrumentsEntrance() {
        instruments.forEachIndexed { index, _ ->
            if (index < instrumentContainer.childCount) {
                val button = instrumentContainer.getChildAt(index)
                Handler(Looper.getMainLooper()).postDelayed({
                    button.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(850)
                        .withStartAction {
                            button.animate()
                                .scaleX(1.3f)
                                .scaleY(1.3f)
                                .setDuration(320)
                                .withEndAction {
                                    button.animate()
                                        .scaleX(0.85f)
                                        .scaleY(0.85f)
                                        .setDuration(160)
                                        .withEndAction {
                                            button.animate()
                                                .scaleX(1f)
                                                .scaleY(1f)
                                                .setDuration(220)
                                                .start()
                                        }
                                        .start()
                                }
                                .start()
                        }
                        .start()
                }, index * 350L)
            }
        }
    }

    private fun animateButtonPress(button: Button, onComplete: () -> Unit) {
        button.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .alpha(0.8f)
            .setDuration(130)
            .withEndAction {
                button.animate()
                    .scaleX(1.15f)
                    .scaleY(1.15f)
                    .alpha(1f)
                    .setDuration(130)
                    .withEndAction {
                        button.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(130)
                            .withEndAction { onComplete() }
                            .start()
                    }
                    .start()
            }
            .start()
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun addFloatingInstrumentEmojis(parent: RelativeLayout) {
        val instrumentEmojis = listOf("♪", "♫", "♬", "🎵")
        val emojiColors = listOf("#8B7EC8", "#A693D4", "#C2B5E8") // Soft purple shades to complement teal header

        repeat(6) { index ->
            val emojiView = TextView(this).apply {
                text = instrumentEmojis.random()
                textSize = (14..18).random().toFloat()
                setTextColor(Color.parseColor(emojiColors.random()))
                alpha = 0.6f
                typeface = Typeface.DEFAULT_BOLD
            }

            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = (50..resources.displayMetrics.widthPixels - 100).random()
                topMargin = (150..resources.displayMetrics.heightPixels - 250).random()
            }

            emojiView.layoutParams = params
            parent.addView(emojiView)
            animateFloatingEmoji(emojiView)
        }
    }

    private fun animateFloatingEmoji(emojiView: TextView) {
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = (5000..9000).random().toLong()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                emojiView.translationY = -40f + (progress * 80f)
                emojiView.translationX = -30f + (progress * 60f)
                emojiView.alpha = 0.3f + (progress * 0.4f)
                emojiView.rotation = progress * 180f
                emojiView.scaleX = 0.8f + (progress * 0.4f)
                emojiView.scaleY = 0.8f + (progress * 0.4f)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({ animator.start() }, (0..3000).random().toLong())
    }

    private fun createEnhancedCircularBackground(startColor: String, endColor: String): GradientDrawable {
        return GradientDrawable().apply {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            colors = intArrayOf(
                Color.parseColor(startColor),
                Color.parseColor(endColor),
                Color.parseColor(startColor)
            )
            shape = GradientDrawable.OVAL
            setStroke(8, Color.parseColor("#FFFFFF"))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}

