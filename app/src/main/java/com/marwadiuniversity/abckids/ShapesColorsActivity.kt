package com.marwadiuniversity.abckids

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.AnimatorSet
import android.view.View
import android.content.pm.ActivityInfo
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.*

class ShapesColorsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var btnBack: ImageView
    private lateinit var tabShapes: CardView
    private lateinit var tabColors: CardView
    private lateinit var contentContainer: GridLayout
    private lateinit var progressText: TextView
    private lateinit var mainScrollView: ScrollView

    private var textToSpeech: TextToSpeech? = null
    private var isTTSReady = false
    private var isCurrentlySpeaking = false
    private var currentTab = "shapes"
    private val mainHandler = Handler(Looper.getMainLooper())

    private val shapesData = listOf(
        ShapeItem("Circle", "●", "#FF6B6B", "#FF8E8E"),
        ShapeItem("Square", "■", "#4ECDC4", "#7EDDD7"),
        ShapeItem("Triangle", "▲", "#45B7D1", "#74C7E3"),
        ShapeItem("Rectangle", "▬", "#96CEB4", "#B8DBC4"),
        ShapeItem("Star", "★", "#FFEAA7", "#FFF2C7"),
        ShapeItem("Heart", "♥", "#FD79A8", "#FE9BB8"),
        ShapeItem("Diamond", "♦", "#FDCB6E", "#FEDC8E"),
        ShapeItem("Oval", "⬭", "#6C5CE7", "#8B7EEA"),
        ShapeItem("Pentagon", "⬟", "#A29BFE", "#B8B2FE"),
        ShapeItem("Hexagon", "⬢", "#74B9FF", "#95C9FF"),
        ShapeItem("Moon", "☽", "#81ECEC", "#A1F0F0"),
        ShapeItem("Arrow", "→", "#00B894", "#33C7A9")
    )

    private val colorsData = listOf(
        ColorItem("Red", "#E91E63", "#E74C3C", "Like a juicy strawberry!"),
        ColorItem("Blue", "#2980B9", "#3498DB", "Like the beautiful sky!"),
        ColorItem("Green", "#27AE60", "#2ECC71", "Like fresh grass!"),
        ColorItem("Yellow", "#F1C40F", "#F9E79F", "Like bright sunshine!"),
        ColorItem("Orange", "#D35400", "#E67E22", "Like a sweet orange!"),
        ColorItem("Purple", "#8E44AD", "#9B59B6", "Like magical flowers!"),
        ColorItem("Pink", "#C2185B", "#E91E63", "Like cotton candy!"),
        ColorItem("Brown", "#4E342E", "#6D4C41", "Like chocolate cake!"),
        ColorItem("Black", "#000000", "#2C3E50", "Like a moonless night!"),
        ColorItem("White", "#FFFFFF", "#ECF0F1", "Like fluffy clouds!"),
        ColorItem("Gold", "#B7950B", "#F1C40F", "Like a shining medal!"),
        ColorItem("Silver", "#7F8C8D", "#BDC3C7", "Like sparkling stars!")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            supportActionBar?.hide()
            setContentView(R.layout.activity_shapes_colors)

            initViews()
            initTextToSpeech()
            showShapes()
        } catch (e: Exception) {
            finish()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        tabShapes = findViewById(R.id.tab_shapes)
        tabColors = findViewById(R.id.tab_colors)
        contentContainer = findViewById(R.id.content_container)
        progressText = findViewById(R.id.progress_text)
        mainScrollView = findViewById(R.id.main_scroll_view)

        btnBack.setOnClickListener { finish() }

        tabShapes.setOnClickListener {
            if (currentTab != "shapes") {
                currentTab = "shapes"
                updateTabs()
                showShapes()
                speakText("Shapes")
            }
        }

        tabColors.setOnClickListener {
            if (currentTab != "colors") {
                currentTab = "colors"
                updateTabs()
                showColors()
                speakText("Colors")
            }
        }

        updateTabs()
    }

    private fun updateTabs() {
        val activeColor = if (currentTab == "shapes") "#4CAF50" else "#FF9800"
        val inactiveColor = "#F5F5F5"

        tabShapes.setCardBackgroundColor(Color.parseColor(if (currentTab == "shapes") activeColor else inactiveColor))
        tabColors.setCardBackgroundColor(Color.parseColor(if (currentTab == "colors") activeColor else inactiveColor))
        
        progressText.text = if (currentTab == "shapes") "Learning Shapes" else "Learning Colors"
    }

    private fun showShapes() {
        contentContainer.removeAllViews()
        contentContainer.columnCount = 1
        shapesData.forEach { shape ->
            val card = createBeautifulShapeCard(shape)
            val params = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12))
            }
            card.layoutParams = params
            contentContainer.addView(card)
        }
    }

    private fun showColors() {
        contentContainer.removeAllViews()
        contentContainer.columnCount = 2
        colorsData.forEach { color ->
            val card = createBeautifulColorCard(color)
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12))
            }
            card.layoutParams = params
            contentContainer.addView(card)
        }
    }

    private fun createBeautifulShapeCard(shapeItem: ShapeItem): CardView {
        val card = CardView(this).apply {
            radius = dpToPx(24).toFloat()
            cardElevation = dpToPx(8).toFloat()
            setCardBackgroundColor(Color.parseColor("#99FFFFFF"))
            useCompatPadding = true
            
            val border = GradientDrawable().apply {
                cornerRadius = dpToPx(24).toFloat()
                setStroke(dpToPx(2), Color.parseColor("#B0FFFFFF"))
                setColor(Color.parseColor("#80FFFFFF"))
            }
            background = border
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20))
        }

        val iconContainer = CardView(this).apply {
            radius = dpToPx(40).toFloat()
            layoutParams = LinearLayout.LayoutParams(dpToPx(80), dpToPx(80)).apply {
                marginEnd = dpToPx(20)
            }
            background = GradientDrawable(GradientDrawable.Orientation.TL_BR, 
                intArrayOf(Color.parseColor(shapeItem.primaryColor), Color.parseColor(shapeItem.secondaryColor))).apply {
                shape = GradientDrawable.OVAL
            }
        }

        val iconView = TextView(this).apply {
            text = shapeItem.symbol
            textSize = 32f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        iconContainer.addView(iconView)

        val nameView = TextView(this).apply {
            text = shapeItem.name
            textSize = 22f
            setTextColor(Color.parseColor("#2C3E50"))
            typeface = Typeface.DEFAULT_BOLD
        }

        layout.addView(iconContainer)
        layout.addView(nameView)
        card.addView(layout)

        card.setOnClickListener { showShapePopup(shapeItem) }
        return card
    }

    private fun showShapePopup(shapeItem: ShapeItem) {
        val dialog = android.app.Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        val container = RelativeLayout(this).apply {
            setBackgroundColor(Color.parseColor("#CC000000"))
            setOnClickListener { dialog.dismiss() }
        }

        val popupCard = CardView(this).apply {
            radius = dpToPx(40).toFloat()
            cardElevation = dpToPx(20).toFloat()
            setCardBackgroundColor(Color.WHITE)
            layoutParams = RelativeLayout.LayoutParams(dpToPx(300), dpToPx(400)).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dpToPx(32), dpToPx(32), dpToPx(32), dpToPx(32))
        }

        val bigIcon = CardView(this).apply {
            radius = dpToPx(90).toFloat()
            layoutParams = LinearLayout.LayoutParams(dpToPx(180), dpToPx(180)).apply { bottomMargin = dpToPx(24) }
            background = GradientDrawable(GradientDrawable.Orientation.TL_BR, 
                intArrayOf(Color.parseColor(shapeItem.primaryColor), Color.parseColor(shapeItem.secondaryColor))).apply {
                shape = GradientDrawable.OVAL
            }
        }
        val bigSymbol = TextView(this).apply {
            text = shapeItem.symbol
            textSize = 80f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        bigIcon.addView(bigSymbol)

        val bigName = TextView(this).apply {
            text = shapeItem.name
            textSize = 36f
            setTextColor(Color.parseColor("#2C3E50"))
            typeface = Typeface.DEFAULT_BOLD
        }

        layout.addView(bigIcon)
        layout.addView(bigName)
        popupCard.addView(layout)
        container.addView(popupCard)
        dialog.setContentView(container)
        dialog.show()

        popupCard.scaleX = 0f
        popupCard.scaleY = 0f
        popupCard.animate().scaleX(1f).scaleY(1f).setDuration(400).setInterpolator(OvershootInterpolator()).start()
        
        speakText(shapeItem.name)
    }

    private fun createBeautifulColorCard(color: ColorItem): CardView {
        val card = CardView(this).apply {
            radius = dpToPx(20).toFloat()
            cardElevation = dpToPx(6).toFloat()
            setCardBackgroundColor(Color.parseColor("#CCFFFFFF"))
            useCompatPadding = true
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dpToPx(16), dpToPx(20), dpToPx(16), dpToPx(20))
        }

        val colorPreview = CardView(this).apply {
            radius = dpToPx(30).toFloat()
            layoutParams = LinearLayout.LayoutParams(dpToPx(60), dpToPx(60)).apply { bottomMargin = dpToPx(10) }
            setCardBackgroundColor(Color.parseColor(color.primaryColor))
        }

        val nameView = TextView(this).apply {
            text = color.name
            textSize = 18f
            setTextColor(Color.parseColor("#2C3E50"))
            typeface = Typeface.DEFAULT_BOLD
        }

        layout.addView(colorPreview)
        layout.addView(nameView)
        card.addView(layout)

        card.setOnClickListener { showColorPopup(color) }
        return card
    }

    private fun showColorPopup(color: ColorItem) {
        val dialog = android.app.Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        val container = RelativeLayout(this).apply {
            setBackgroundColor(Color.parseColor("#CC000000"))
            setOnClickListener { dialog.dismiss() }
        }

        val popupCard = CardView(this).apply {
            radius = dpToPx(40).toFloat()
            cardElevation = dpToPx(20).toFloat()
            setCardBackgroundColor(Color.WHITE)
            layoutParams = RelativeLayout.LayoutParams(dpToPx(300), dpToPx(400)).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dpToPx(32), dpToPx(32), dpToPx(32), dpToPx(32))
        }

        val bigColor = CardView(this).apply {
            radius = dpToPx(80).toFloat()
            layoutParams = LinearLayout.LayoutParams(dpToPx(160), dpToPx(160)).apply { bottomMargin = dpToPx(24) }
            setCardBackgroundColor(Color.parseColor(color.primaryColor))
        }

        val bigName = TextView(this).apply {
            text = color.name
            textSize = 36f
            setTextColor(Color.parseColor("#2C3E50"))
            typeface = Typeface.DEFAULT_BOLD
        }

        val descView = TextView(this).apply {
            text = color.description
            textSize = 16f
            setTextColor(Color.parseColor("#7F8C8D"))
            gravity = Gravity.CENTER
            setPadding(0, dpToPx(10), 0, 0)
        }

        layout.addView(bigColor)
        layout.addView(bigName)
        layout.addView(descView)
        popupCard.addView(layout)
        container.addView(popupCard)
        dialog.setContentView(container)
        dialog.show()

        popupCard.scaleX = 0f
        popupCard.scaleY = 0f
        popupCard.animate().scaleX(1f).scaleY(1f).setDuration(400).setInterpolator(OvershootInterpolator()).start()
        
        speakText("${color.name}. ${color.description}")
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                tts.language = Locale.US
                tts.setSpeechRate(0.8f)
                tts.setPitch(1.1f)
                isTTSReady = true
            }
        }
    }

    private fun speakText(text: String) {
        if (isTTSReady) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ShapeSpeak")
        }
    }

    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    data class ShapeItem(val name: String, val symbol: String, val primaryColor: String, val secondaryColor: String)
    data class ColorItem(val name: String, val primaryColor: String, val secondaryColor: String, val description: String)
}
