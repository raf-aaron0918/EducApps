package com.marwadiuniversity.abckids

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.TextView
import com.marwadiuniversity.abckids.utils.InstrumentSynthesizer

class PianoActivity : Activity() {

    private var backgroundMusicPlayer: MediaPlayer? = null
    private var isBackgroundPlaying = false
    private var currentMusicTrack = -1
    private var selectedMusicButton: TextView? = null
    private val musicButtons = mutableListOf<TextView>()
    private var instrumentSynthesizer: InstrumentSynthesizer? = null

    // Piano sound players - reuse to avoid memory issues
    private val pianoPlayers = mutableMapOf<String, MediaPlayer?>()

    // Piano keys references for drag detection
    private val pianoKeys = mutableListOf<TextView>()
    private val lastTouchedKeys = mutableMapOf<Int, TextView?>()
    private var isDragging = false

    // Store original backgrounds for each key
    private val keyOriginalBackgrounds = mutableMapOf<TextView, GradientDrawable>()

    private val noteFiles = mapOf(
        "C" to R.raw.piano_c,
        "D" to R.raw.piano_d,
        "E" to R.raw.piano_e,
        "F" to R.raw.piano_f,
        "G" to R.raw.piano_g,
        "A" to R.raw.piano_a,
        "B" to R.raw.piano_b,
        "C2" to R.raw.piano_c2
    )

    private val musicTracks = listOf(
        R.raw.happy_song,
        R.raw.classical_song,
        R.raw.kids_song,
        R.raw.lullaby_song,
        R.raw.upbeat_song,
        R.raw.calm_song
    )

    private val buttonColors = listOf("#FF4444", "#44FF44", "#AA44FF", "#44AAFF", "#FFAA44", "#FF44AA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            instrumentSynthesizer = InstrumentSynthesizer(this)

            // Initialize piano sound players
            initializePianoPlayers()

            setupUI()
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error in onCreate", e)
            finish()
        }
    }

    private fun initializePianoPlayers() {
        noteFiles.forEach { (note, resourceId) ->
            try {
                val mediaPlayer = MediaPlayer.create(this, resourceId)
                if (mediaPlayer != null) {
                    mediaPlayer.setOnCompletionListener {
                        try {
                            it.seekTo(0) // Reset to beginning for next play
                        } catch (e: Exception) {
                            Log.e("PianoActivity", "Error resetting player for $note", e)
                        }
                    }
                    pianoPlayers[note] = mediaPlayer
                } else {
                    Log.w("PianoActivity", "Could not create MediaPlayer for $note")
                    pianoPlayers[note] = null
                }
            } catch (e: Exception) {
                Log.e("PianoActivity", "Error initializing sound for $note", e)
                pianoPlayers[note] = null
            }
        }
    }

    private fun setupUI() {
        try {
            val mainContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundResource(R.drawable.background)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // Create header
            val header = createHeader()
            mainContainer.addView(header)

            // Create piano container with original gold/orange gradient
            val pianoContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val drawable = GradientDrawable().apply {
                    cornerRadius = 30f
                    colors = intArrayOf(
                        Color.parseColor("#FFD700"), // Gold
                        Color.parseColor("#FFA500")  // Orange
                    )
                }
                background = drawable
                setPadding(25, 25, 25, 25)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                ).apply {
                    setMargins(dpToPx(15), dpToPx(5), dpToPx(15), dpToPx(10))
                }
            }

            // Top control buttons
            val topButtonsContainer = createTopButtons()
            pianoContainer.addView(topButtonsContainer)

            // Piano keys container (now using RelativeLayout for overlap support)
            val keysContainer = createPianoKeys()
            pianoContainer.addView(keysContainer)

            mainContainer.addView(pianoContainer)
            setContentView(mainContainer)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error setting up UI", e)
        }
    }

    private fun createHeader(): LinearLayout {
        val headerContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = resources.getDrawable(android.R.color.transparent, null)
        }

        try {
            val topRow = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 14, 16, 8)
                gravity = Gravity.CENTER_VERTICAL
            }

            val backButton = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(44), dpToPx(44))
                setImageResource(R.drawable.ic_back)
                setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10))
                setBackgroundResource(R.drawable.quiz_surface_bg)
                elevation = 4f
                contentDescription = "Back"
                setOnClickListener {
                    try {
                        finish()
                    } catch (e: Exception) {
                        Log.e("PianoActivity", "Error handling back button", e)
                    }
                }
            }
            topRow.addView(backButton)

            val flexibleSpace = Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, 1, 1f)
            }
            topRow.addView(flexibleSpace)

            val fixedSpace = Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(44), dpToPx(44))
            }
            topRow.addView(fixedSpace)

            headerContainer.addView(topRow)

                        // Title removed to maximize piano size
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error creating header", e)
        }

        return headerContainer
    }

    private fun createTopButtons(): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20
            }
        }

        try {
            // Speaker grilles on sides
            val leftSpeaker = createSpeakerGrille()
            val rightSpeaker = createSpeakerGrille()

            // Control buttons
            val buttonIcons = listOf("×", "♪", "♫", "♪", "♫", "♪")

            val buttonsContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            buttonColors.forEachIndexed { index, color ->
                val button = createControlButton(color, buttonIcons[index], index)
                buttonsContainer.addView(button)
                musicButtons.add(button)
            }

            container.addView(leftSpeaker)
            container.addView(buttonsContainer)
            container.addView(rightSpeaker)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error creating top buttons", e)
        }

        return container
    }

    private fun createSpeakerGrille(): LinearLayout {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(60),
                dpToPx(80)
            )
            gravity = Gravity.CENTER
        }

        try {
            repeat(6) {
                val line = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        dpToPx(40),
                        dpToPx(4)
                    ).apply {
                        setMargins(0, dpToPx(2), 0, dpToPx(2))
                    }
                    setBackgroundColor(Color.parseColor("#8B4513"))
                }
                container.addView(line)
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error creating speaker grille", e)
        }

        return container
    }

    private fun createControlButton(colorHex: String, icon: String, index: Int): TextView {
        return TextView(this).apply {
            text = icon
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            tag = index // Store index for later reference
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(50),
                dpToPx(50)
            ).apply {
                setMargins(dpToPx(5), 0, dpToPx(5), 0)
            }

            updateButtonAppearance(colorHex, false)

            setOnTouchListener { v, event ->
                try {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            scaleX = 0.9f
                            scaleY = 0.9f
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            scaleX = 1.0f
                            scaleY = 1.0f
                            handleMusicButtonClick(this, index, colorHex)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PianoActivity", "Error handling button touch", e)
                }
                true
            }
        }
    }

    private fun TextView.updateButtonAppearance(colorHex: String, isSelected: Boolean) {
        try {
            val drawable = GradientDrawable().apply {
                cornerRadius = 15f
                if (isSelected) {
                    setColor(Color.WHITE)
                    setStroke(4, Color.parseColor(colorHex))
                } else {
                    setColor(Color.parseColor(colorHex))
                    setStroke(3, Color.parseColor("#FF8C00"))
                }
            }
            background = drawable
            setTextColor(if (isSelected) Color.parseColor(colorHex) else Color.WHITE)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error updating button appearance", e)
        }
    }

    private fun handleMusicButtonClick(button: TextView, buttonIndex: Int, colorHex: String) {
        try {
            if (buttonIndex == 0) {
                // Stop button clicked
                stopBackgroundMusic()
                return
            }

            val musicIndex = buttonIndex - 1
            if (currentMusicTrack == musicIndex && isBackgroundPlaying) {
                // Currently playing track button clicked - stop it
                stopBackgroundMusic()
            } else {
                // Different track or no music playing - start new track
                stopBackgroundMusic()
                playBackgroundMusic(musicIndex, button, colorHex)
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error handling music button click", e)
        }
    }

    private fun playBackgroundMusic(musicIndex: Int, button: TextView, colorHex: String) {
        try {
            if (musicIndex < musicTracks.size) {
                backgroundMusicPlayer = MediaPlayer.create(this, musicTracks[musicIndex])
                backgroundMusicPlayer?.let { player ->
                    player.isLooping = true
                    player.setOnPreparedListener {
                        try {
                            it.start()
                            isBackgroundPlaying = true
                            currentMusicTrack = musicIndex

                            // Update button appearance - reset previous button
                            selectedMusicButton?.let { prevButton ->
                                val prevIndex = prevButton.tag as? Int ?: 0
                                prevButton.updateButtonAppearance(getButtonColorByIndex(prevIndex), false)
                            }

                            // Update current button
                            button.updateButtonAppearance(colorHex, true)
                            selectedMusicButton = button
                        } catch (e: Exception) {
                            Log.e("PianoActivity", "Error starting background music", e)
                        }
                    }
                    player.setOnErrorListener { _, what, extra ->
                        Log.e("PianoActivity", "MediaPlayer error: what=$what, extra=$extra")
                        stopBackgroundMusic()
                        true
                    }
                } ?: run {
                    Log.e("PianoActivity", "Could not create MediaPlayer for track $musicIndex")
                }
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error starting background music", e)
            stopBackgroundMusic()
        }
    }

    private fun stopBackgroundMusic() {
        try {
            backgroundMusicPlayer?.let {
                try {
                    if (it.isPlaying) {
                        it.stop()
                    }
                    it.release()
                } catch (e: Exception) {
                    Log.e("PianoActivity", "Error stopping background music", e)
                }
            }
            backgroundMusicPlayer = null
            isBackgroundPlaying = false

            // Reset button appearance
            selectedMusicButton?.let { button ->
                val index = button.tag as? Int ?: 0
                button.updateButtonAppearance(getButtonColorByIndex(index), false)
            }
            selectedMusicButton = null
            currentMusicTrack = -1
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error in stopBackgroundMusic", e)
        }
    }

    private fun getButtonColorByIndex(index: Int): String {
        return if (index in buttonColors.indices) buttonColors[index] else "#FF4444"
    }

    private fun createPianoKeys(): RelativeLayout {
        val rootContainer = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        try {
            val whiteNotes = listOf("C", "D", "E", "F", "G", "A", "B", "C2")
            val whiteKeyCount = whiteNotes.size
            
            // White keys layout
            val whiteKeysLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                
                val drawable = GradientDrawable().apply {
                    cornerRadii = floatArrayOf(
                        dpToPx(25).toFloat(), dpToPx(25).toFloat(),
                        dpToPx(25).toFloat(), dpToPx(25).toFloat(),
                        dpToPx(8).toFloat(), dpToPx(8).toFloat(),
                        dpToPx(8).toFloat(), dpToPx(8).toFloat()
                    )
                    colors = intArrayOf(Color.parseColor("#FF4500"), Color.parseColor("#FF6347"))
                }
                background = drawable
                setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
            }
            rootContainer.addView(whiteKeysLayout)

            val whiteKeyColors = listOf(
                "#FF4444", "#FF8844", "#FFFF44", "#88FF88",
                "#44FF44", "#44FFFF", "#4488FF", "#8844FF"
            )

            whiteNotes.forEachIndexed { index, note ->
                val key = createPianoKey(note, whiteKeyColors[index], true)
                whiteKeysLayout.addView(key)
                pianoKeys.add(key)
            }

            // Black keys overlay
            whiteKeysLayout.post {
                try {
                    val keyWidth = whiteKeysLayout.width / whiteKeyCount
                    val blackKeyWidth = (keyWidth * 0.6).toInt()
                    val blackKeyHeight = (whiteKeysLayout.height * 0.6).toInt()

                    val blackNotes = listOf(
                        Pair("C#", 1), Pair("D#", 2), 
                        Pair("F#", 4), Pair("G#", 5), Pair("A#", 6)
                    )

                    blackNotes.forEach { (note, position) ->
                        val blackKey = createPianoKey(note, "#000000", false).apply {
                            layoutParams = RelativeLayout.LayoutParams(blackKeyWidth, blackKeyHeight).apply {
                                leftMargin = (keyWidth * position) - (blackKeyWidth / 2) + dpToPx(5)
                                topMargin = dpToPx(5)
                            }
                        }
                        rootContainer.addView(blackKey)
                        pianoKeys.add(blackKey)
                    }
                } catch (e: Exception) {
                    Log.e("PianoActivity", "Error adding black keys", e)
                }
            }

            setupGlobalTouchListener(rootContainer)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error creating piano keys", e)
        }

        return rootContainer
    }

    private fun setupGlobalTouchListener(container: RelativeLayout) {
        container.setOnTouchListener { _, event ->
            try {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        isDragging = true
                        val pointerIndex = event.actionIndex
                        handleTouchAtPosition(event.getX(pointerIndex), event.getY(pointerIndex), event.getPointerId(pointerIndex))
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isDragging) {
                            for (i in 0 until event.pointerCount) {
                                handleTouchAtPosition(event.getX(i), event.getY(i), event.getPointerId(i))
                            }
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                        val pointerId = event.getPointerId(event.actionIndex)
                        lastTouchedKeys[pointerId]?.resetKeyVisual()
                        lastTouchedKeys.remove(pointerId)
                        
                        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                            isDragging = false
                            resetAllKeysVisual()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("PianoActivity", "Error in global touch listener", e)
            }
            true
        }
    }

    private fun handleTouchAtPosition(x: Float, y: Float, pointerId: Int) {
        try {
            val touchedKey = findKeyAtPosition(x, y)
            val lastKey = lastTouchedKeys[pointerId]

            if (touchedKey != null && touchedKey != lastKey) {
                lastKey?.resetKeyVisual()
                val note = touchedKey.tag.toString()
                playPianoNote(note)
                touchedKey.applyPressedVisual()
                touchedKey.addSparkleEffect()
                lastTouchedKeys[pointerId] = touchedKey
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error handling touch at position", e)
        }
    }

    private fun findKeyAtPosition(x: Float, y: Float): TextView? {
        try {
            // Screen locations for accurate hit detection
            for (i in pianoKeys.size - 1 downTo 0) {
                val key = pianoKeys[i]
                val location = IntArray(2)
                key.getLocationOnScreen(location)
                
                val parentLocation = IntArray(2)
                (key.parent as View).getLocationOnScreen(parentLocation)
                
                val screenTouchX = x + parentLocation[0]
                val screenTouchY = y + parentLocation[1]

                if (screenTouchX >= location[0] && screenTouchX <= location[0] + key.width &&
                    screenTouchY >= location[1] && screenTouchY <= location[1] + key.height) {
                    return key
                }
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error finding key at position", e)
        }
        return null
    }

    private fun createPianoKey(note: String, colorHex: String, isWhite: Boolean): TextView {
        return TextView(this).apply {
            text = note
            tag = note // Use tag for note lookup
            textSize = if (isWhite) 24f else 14f
            setTextColor(Color.WHITE)
            gravity = if (isWhite) Gravity.CENTER else Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            if (!isWhite) setPadding(0, 0, 0, dpToPx(10))
            
            if (isWhite) {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                    setMargins(dpToPx(4), 0, dpToPx(4), 0)
                }
            }

            try {
                val drawable = GradientDrawable().apply {
                    cornerRadius = dpToPx(if (isWhite) 15 else 8).toFloat()
                    if (isWhite) {
                        colors = intArrayOf(
                            Color.parseColor(colorHex),
                            adjustBrightness(Color.parseColor(colorHex), 0.8f)
                        )
                        setStroke(2, Color.WHITE)
                    } else {
                        colors = intArrayOf(Color.parseColor("#444444"), Color.parseColor("#000000"))
                        setStroke(1, Color.BLACK)
                    }
                }
                background = drawable
                keyOriginalBackgrounds[this] = drawable
                elevation = dpToPx(if (isWhite) 4 else 8).toFloat()
            } catch (e: Exception) {
                Log.e("PianoActivity", "Error setting up key background", e)
            }
        }
    }

    private fun TextView.applyPressedVisual() {
        try {
            scaleX = 0.95f
            scaleY = 0.95f
            alpha = 0.8f
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error applying pressed visual", e)
        }
    }

    private fun TextView.resetKeyVisual() {
        try {
            scaleX = 1.0f
            scaleY = 1.0f
            alpha = 1.0f
            keyOriginalBackgrounds[this]?.let { background = it }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error resetting key visual", e)
        }
    }

    private fun resetAllKeysVisual() {
        pianoKeys.forEach { it.resetKeyVisual() }
    }

    private fun playPianoNote(note: String) {
        try {
            instrumentSynthesizer?.playPianoSound(note)
            pianoPlayers[note]?.let { player ->
                if (player.isPlaying) player.seekTo(0) else player.start()
            }
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error playing piano note: $note", e)
        }
    }

    private fun TextView.addSparkleEffect() {
        try {
            val original = background
            background = GradientDrawable().apply {
                cornerRadius = 15f
                setColor(Color.WHITE)
                alpha = 180
            }
            postDelayed({ background = keyOriginalBackgrounds[this] ?: original }, 100)
        } catch (e: Exception) {
            Log.e("PianoActivity", "Error adding sparkle", e)
        }
    }

    private fun adjustBrightness(color: Int, factor: Float): Int {
        val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    override fun onPause() {
        super.onPause()
        backgroundMusicPlayer?.let { if (it.isPlaying) it.pause() }
    }

    override fun onResume() {
        super.onResume()
        if (isBackgroundPlaying) backgroundMusicPlayer?.start()
    }

    override fun onDestroy() {
        stopBackgroundMusic()
        pianoPlayers.values.forEach { it?.release() }
        pianoPlayers.clear()
        super.onDestroy()
    }
}
