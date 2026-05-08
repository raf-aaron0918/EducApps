package com.marwadiuniversity.abckids

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object TemplateSketchFactory {
    fun createTemplate(templateResId: Int, width: Int = 800, height: Int = 800): Bitmap {
        val safeW = width.coerceAtLeast(200)
        val safeH = height.coerceAtLeast(200)
        val bitmap = Bitmap.createBitmap(safeW, safeH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL; color = Color.WHITE }
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; color = Color.BLACK; strokeWidth = (safeW * 0.01f).coerceAtLeast(4f) }
        canvas.drawColor(Color.WHITE)

        when (templateResId) {
            R.drawable.templete_house -> drawHouse(canvas, fill, stroke, safeW, safeH)
            R.drawable.templete_fish -> drawFish(canvas, fill, stroke, safeW, safeH)
            R.drawable.templete_duck -> drawDuck(canvas, fill, stroke, safeW, safeH)
            R.drawable.templete_turtle -> drawTurtle(canvas, fill, stroke, safeW, safeH)
            R.drawable.templete_flower -> drawFlower(canvas, fill, stroke, safeW, safeH)
            R.drawable.templete_icecream -> drawIceCream(canvas, fill, stroke, safeW, safeH)
            R.drawable.templete_unicorn -> drawUnicorn(canvas, fill, stroke, safeW, safeH)
            R.drawable.templete_dolphin -> drawDolphin(canvas, fill, stroke, safeW, safeH)
            R.drawable.templete_plant -> drawPlant(canvas, fill, stroke, safeW, safeH)
            else -> drawCastle(canvas, fill, stroke, safeW, safeH)
        }
        return bitmap
    }

    private fun drawHouse(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        val roof = Path().apply {
            moveTo(w * 0.2f, h * 0.45f); lineTo(w * 0.5f, h * 0.2f); lineTo(w * 0.8f, h * 0.45f); close()
        }
        c.drawPath(roof, f); c.drawPath(roof, s)
        c.drawRect(w * 0.25f, h * 0.45f, w * 0.75f, h * 0.82f, f); c.drawRect(w * 0.25f, h * 0.45f, w * 0.75f, h * 0.82f, s)
        c.drawRect(w * 0.45f, h * 0.6f, w * 0.55f, h * 0.82f, f); c.drawRect(w * 0.45f, h * 0.6f, w * 0.55f, h * 0.82f, s)
        c.drawRect(w * 0.3f, h * 0.55f, w * 0.4f, h * 0.67f, f); c.drawRect(w * 0.3f, h * 0.55f, w * 0.4f, h * 0.67f, s)
    }
    private fun drawFish(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        c.drawOval(RectF(w * 0.2f, h * 0.35f, w * 0.7f, h * 0.7f), f); c.drawOval(RectF(w * 0.2f, h * 0.35f, w * 0.7f, h * 0.7f), s)
        val tail = Path().apply { moveTo(w * 0.7f, h * 0.52f); lineTo(w * 0.9f, h * 0.38f); lineTo(w * 0.9f, h * 0.66f); close() }
        c.drawPath(tail, f); c.drawPath(tail, s); c.drawCircle(w * 0.33f, h * 0.5f, w * 0.03f, s)
    }
    private fun drawDuck(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        c.drawOval(RectF(w * 0.2f, h * 0.45f, w * 0.75f, h * 0.78f), f); c.drawOval(RectF(w * 0.2f, h * 0.45f, w * 0.75f, h * 0.78f), s)
        c.drawCircle(w * 0.68f, h * 0.4f, w * 0.09f, f); c.drawCircle(w * 0.68f, h * 0.4f, w * 0.09f, s)
        c.drawRect(w * 0.75f, h * 0.4f, w * 0.88f, h * 0.46f, f); c.drawRect(w * 0.75f, h * 0.4f, w * 0.88f, h * 0.46f, s)
    }
    private fun drawTurtle(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        c.drawOval(RectF(w * 0.22f, h * 0.38f, w * 0.78f, h * 0.76f), f); c.drawOval(RectF(w * 0.22f, h * 0.38f, w * 0.78f, h * 0.76f), s)
        c.drawCircle(w * 0.8f, h * 0.55f, w * 0.06f, f); c.drawCircle(w * 0.8f, h * 0.55f, w * 0.06f, s)
        listOf(0.28f to 0.75f, 0.42f to 0.79f, 0.58f to 0.79f, 0.72f to 0.75f).forEach { (x,y) -> c.drawCircle(w*x, h*y, w*0.04f, f); c.drawCircle(w*x, h*y, w*0.04f, s) }
    }
    private fun drawFlower(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        c.drawRect(w * 0.48f, h * 0.4f, w * 0.52f, h * 0.8f, s)
        listOf(Pair(0.5f,0.35f), Pair(0.42f,0.42f), Pair(0.58f,0.42f), Pair(0.42f,0.28f), Pair(0.58f,0.28f)).forEach { (x,y) ->
            c.drawCircle(w*x, h*y, w*0.09f, f); c.drawCircle(w*x, h*y, w*0.09f, s)
        }
        c.drawCircle(w * 0.5f, h * 0.35f, w * 0.06f, s)
    }
    private fun drawIceCream(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        val cone = Path().apply { moveTo(w*0.4f, h*0.78f); lineTo(w*0.6f, h*0.78f); lineTo(w*0.5f, h*0.95f); close() }
        c.drawPath(cone, f); c.drawPath(cone, s)
        c.drawCircle(w * 0.5f, h * 0.52f, w * 0.14f, f); c.drawCircle(w * 0.5f, h * 0.52f, w * 0.14f, s)
    }
    private fun drawUnicorn(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        c.drawOval(RectF(w * 0.25f, h * 0.35f, w * 0.75f, h * 0.75f), f); c.drawOval(RectF(w * 0.25f, h * 0.35f, w * 0.75f, h * 0.75f), s)
        val horn = Path().apply { moveTo(w*0.5f, h*0.18f); lineTo(w*0.45f, h*0.36f); lineTo(w*0.55f, h*0.36f); close() }
        c.drawPath(horn, f); c.drawPath(horn, s)
        c.drawCircle(w * 0.43f, h * 0.5f, w * 0.02f, s); c.drawCircle(w * 0.57f, h * 0.5f, w * 0.02f, s)
    }
    private fun drawDolphin(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        val body = Path().apply { moveTo(w*0.2f,h*0.6f); quadTo(w*0.45f,h*0.25f,w*0.8f,h*0.5f); quadTo(w*0.55f,h*0.8f,w*0.2f,h*0.6f); close() }
        c.drawPath(body, f); c.drawPath(body, s)
        c.drawCircle(w * 0.66f, h * 0.48f, w * 0.02f, s)
    }
    private fun drawPlant(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        c.drawRect(w * 0.35f, h * 0.75f, w * 0.65f, h * 0.88f, f); c.drawRect(w * 0.35f, h * 0.75f, w * 0.65f, h * 0.88f, s)
        c.drawRect(w * 0.49f, h * 0.4f, w * 0.51f, h * 0.75f, s)
        c.drawOval(RectF(w * 0.42f, h * 0.45f, w * 0.5f, h * 0.58f), f); c.drawOval(RectF(w * 0.42f, h * 0.45f, w * 0.5f, h * 0.58f), s)
        c.drawOval(RectF(w * 0.5f, h * 0.5f, w * 0.58f, h * 0.63f), f); c.drawOval(RectF(w * 0.5f, h * 0.5f, w * 0.58f, h * 0.63f), s)
    }
    private fun drawCastle(c: Canvas, f: Paint, s: Paint, w: Int, h: Int) {
        c.drawRect(w * 0.22f, h * 0.42f, w * 0.78f, h * 0.85f, f); c.drawRect(w * 0.22f, h * 0.42f, w * 0.78f, h * 0.85f, s)
        c.drawRect(w * 0.3f, h * 0.3f, w * 0.42f, h * 0.42f, f); c.drawRect(w * 0.3f, h * 0.3f, w * 0.42f, h * 0.42f, s)
        c.drawRect(w * 0.58f, h * 0.3f, w * 0.7f, h * 0.42f, f); c.drawRect(w * 0.58f, h * 0.3f, w * 0.7f, h * 0.42f, s)
        c.drawRect(w * 0.46f, h * 0.62f, w * 0.54f, h * 0.85f, f); c.drawRect(w * 0.46f, h * 0.62f, w * 0.54f, h * 0.85f, s)
    }
}

class ColorFillView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var originalBitmap: Bitmap? = null
    private var workingBitmap: Bitmap? = null
    private var canvasRef: Canvas? = null
    private var paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    private var currentColor = Color.RED
    private val tolerance = 50
    private val maxBitmapSize = 800
    private var isProcessing = false
    private var fillJob: Job? = null

    fun loadTemplate(templateResId: Int) {
        try {
            fillJob?.cancel()
            isProcessing = false

            cleanupBitmaps()

            var bitmap = loadBitmapSafely(templateResId)
            if (bitmap == null || isLikelyPlaceholder(bitmap)) {
                bitmap?.recycle()
                bitmap = TemplateSketchFactory.createTemplate(templateResId)
            }
            if (bitmap != null) {
                val croppedBitmap = cropBottomPortion(bitmap, 0.90f)
                if (croppedBitmap != bitmap) {
                    bitmap.recycle()
                }

                originalBitmap = croppedBitmap
                workingBitmap = croppedBitmap.copy(Bitmap.Config.ARGB_8888, true)

                if (workingBitmap != null) {
                    canvasRef = Canvas(workingBitmap!!)
                    post {
                        invalidate()
                        Log.d("ColorFillView", "Template loaded successfully")
                    }
                } else {
                    createFallbackBitmap()
                }
            } else {
                createFallbackBitmap()
            }
        } catch (e: OutOfMemoryError) {
            Log.e("ColorFillView", "OutOfMemoryError loading template", e)
            handleMemoryError()
        } catch (e: Exception) {
            Log.e("ColorFillView", "Error loading template", e)
            createFallbackBitmap()
        }
    }

    private fun loadBitmapSafely(templateResId: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeResource(context.resources, templateResId, options)

            if (options.outWidth > 0 && options.outHeight > 0) {
                val scaleFactor = calculateScaleFactor(options.outWidth, options.outHeight)
                options.inJustDecodeBounds = false
                options.inSampleSize = scaleFactor
                options.inPreferredConfig = Bitmap.Config.RGB_565

                val bitmap = BitmapFactory.decodeResource(context.resources, templateResId, options)

                if (bitmap == null) {
                    Log.e("ColorFillView", "Bitmap is null after decoding")
                }

                return bitmap
            }

            // Support XML drawables (shape/vector/layer-list) by rendering them to bitmap.
            val drawable = ContextCompat.getDrawable(context, templateResId) ?: return null
            val outW = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else maxBitmapSize
            val outH = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else maxBitmapSize
            val rendered = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(rendered)
            canvas.drawColor(Color.WHITE)
            drawable.setBounds(0, 0, outW, outH)
            drawable.draw(canvas)
            rendered
        } catch (e: Exception) {
            Log.e("ColorFillView", "Failed to load bitmap safely", e)
            null
        }
    }

    private fun isLikelyPlaceholder(bitmap: Bitmap): Boolean {
        if (bitmap.width < 8 || bitmap.height < 8) return true
        val points = listOf(
            bitmap.width / 2 to bitmap.height / 2,
            bitmap.width / 4 to bitmap.height / 4,
            bitmap.width * 3 / 4 to bitmap.height / 4,
            bitmap.width / 4 to bitmap.height * 3 / 4,
            bitmap.width * 3 / 4 to bitmap.height * 3 / 4
        )
        val colors = points.map { (x, y) -> bitmap.getPixel(x, y) }
        val avgR = colors.map { Color.red(it) }.average()
        val avgG = colors.map { Color.green(it) }.average()
        val avgB = colors.map { Color.blue(it) }.average()
        val variance = colors.sumOf {
            val dr = Color.red(it) - avgR
            val dg = Color.green(it) - avgG
            val db = Color.blue(it) - avgB
            dr * dr + dg * dg + db * db
        } / colors.size
        return variance < 50.0
    }

    private fun cropBottomPortion(bitmap: Bitmap, cropRatio: Float): Bitmap {
        return try {
            val newHeight = (bitmap.height * cropRatio).toInt()
            if (newHeight > 0 && newHeight <= bitmap.height) {
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, newHeight)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            Log.e("ColorFillView", "Failed to crop bitmap", e)
            bitmap
        }
    }

    private fun calculateScaleFactor(width: Int, height: Int): Int {
        var scaleFactor = 1
        while (width / scaleFactor > maxBitmapSize || height / scaleFactor > maxBitmapSize) {
            scaleFactor *= 2
        }
        return scaleFactor
    }

    private fun handleMemoryError() {
        cleanupBitmaps()
        System.gc()
        createFallbackBitmap()
        post {
            Toast.makeText(context, "Image too large, using simplified version", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createFallbackBitmap() {
        try {
            cleanupBitmaps()
            originalBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565)
            originalBitmap?.let { bitmap ->
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)

                val paint = Paint().apply {
                    color = Color.BLACK
                    strokeWidth = 3f
                    style = Paint.Style.STROKE
                    isAntiAlias = true
                }

                canvas.drawCircle(200f, 200f, 150f, paint)
                canvas.drawCircle(170f, 170f, 20f, paint)
                canvas.drawCircle(230f, 170f, 20f, paint)

                workingBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                if (workingBitmap != null) {
                    canvasRef = Canvas(workingBitmap!!)
                    post { invalidate() }
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillView", "Failed to create fallback bitmap", e)
        }
    }

    private fun cleanupBitmaps() {
        try {
            originalBitmap?.let {
                if (!it.isRecycled) {
                    try {
                        it.recycle()
                    } catch (e: Exception) {
                        Log.e("ColorFillView", "Error recycling original bitmap", e)
                    }
                }
            }
            workingBitmap?.let {
                if (!it.isRecycled) {
                    try {
                        it.recycle()
                    } catch (e: Exception) {
                        Log.e("ColorFillView", "Error recycling working bitmap", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillView", "Error cleaning up bitmaps", e)
        } finally {
            originalBitmap = null
            workingBitmap = null
            canvasRef = null
        }
    }

    fun setFillColor(color: Int) {
        currentColor = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            workingBitmap?.let { bitmap ->
                if (!bitmap.isRecycled && width > 0 && height > 0) {
                    val rect = Rect(0, 0, width, height)
                    canvas.drawBitmap(bitmap, null, rect, paint)
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillView", "Error drawing bitmap", e)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && !isProcessing) {
            workingBitmap?.let { bitmap ->
                if (bitmap.isRecycled) return false

                val scaleX = bitmap.width.toFloat() / width
                val scaleY = bitmap.height.toFloat() / height
                val x = (event.x * scaleX).toInt()
                val y = (event.y * scaleY).toInt()

                if (x >= 0 && x < bitmap.width && y >= 0 && y < bitmap.height) {
                    performFloodFill(bitmap, x, y, currentColor)
                }
            }
        }
        return true
    }

    private fun performFloodFill(bitmap: Bitmap, x: Int, y: Int, newColor: Int) {
        if (isProcessing || bitmap.isRecycled) return

        isProcessing = true
        fillJob?.cancel()

        fillJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                optimizedFloodFill(bitmap, x, y, newColor)
                withContext(Dispatchers.Main) {
                    invalidate()
                }
            } catch (e: CancellationException) {
                Log.d("ColorFillView", "Fill operation cancelled")
            } catch (e: Exception) {
                Log.e("ColorFillView", "Fill operation failed", e)
            } finally {
                isProcessing = false
            }
        }
    }

    private fun optimizedFloodFill(bitmap: Bitmap, startX: Int, startY: Int, newColor: Int) {
        if (bitmap.isRecycled) return

        try {
            val targetColor = bitmap.getPixel(startX, startY)
            if (targetColor == newColor) return
            if (!isValidFillTarget(targetColor)) return

            val width = bitmap.width
            val height = bitmap.height
            val visited = BooleanArray(width * height)
            val queue = ArrayDeque<Point>()

            queue.offer(Point(startX, startY))
            var pixelsProcessed = 0
            val maxPixels = width * height / 4

            while (queue.isNotEmpty() && pixelsProcessed < maxPixels) {
                val point = queue.poll() ?: break
                val x = point.x
                val y = point.y

                if (x < 0 || x >= width || y < 0 || y >= height) continue

                val index = y * width + x
                if (visited[index]) continue

                val currentPixel = bitmap.getPixel(x, y)
                if (!isColorSimilar(currentPixel, targetColor, tolerance)) continue

                visited[index] = true
                bitmap.setPixel(x, y, newColor)
                pixelsProcessed++

                if (queue.size < 5000) {
                    queue.offer(Point(x + 1, y))
                    queue.offer(Point(x - 1, y))
                    queue.offer(Point(x, y + 1))
                    queue.offer(Point(x, y - 1))
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillView", "Error in flood fill", e)
        }
    }

    private fun isValidFillTarget(color: Int): Boolean {
        return isColorSimilar(color, Color.WHITE, tolerance) ||
                isColorSimilar(color, Color.TRANSPARENT, tolerance) ||
                Color.alpha(color) < 128
    }

    private fun isColorSimilar(color1: Int, color2: Int, tolerance: Int): Boolean {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)

        return abs(r1 - r2) <= tolerance &&
                abs(g1 - g2) <= tolerance &&
                abs(b1 - b2) <= tolerance
    }

    fun clearCanvas() {
        if (isProcessing) return

        try {
            fillJob?.cancel()
            originalBitmap?.let { original ->
                if (!original.isRecycled) {
                    workingBitmap?.let {
                        if (!it.isRecycled) {
                            it.recycle()
                        }
                    }
                    workingBitmap = original.copy(Bitmap.Config.ARGB_8888, true)
                    if (workingBitmap != null) {
                        canvasRef = Canvas(workingBitmap!!)
                        invalidate()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillView", "Error clearing canvas", e)
        }
    }

    fun saveColoring(): Bitmap? {
        return try {
            workingBitmap?.takeIf { !it.isRecycled }
        } catch (e: Exception) {
            Log.e("ColorFillView", "Error saving coloring", e)
            null
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {
            fillJob?.cancel()
            cleanupBitmaps()
        } catch (e: Exception) {
            Log.e("ColorFillView", "Error in onDetachedFromWindow", e)
        }
    }
}

class ColorFillActivity : AppCompatActivity() {

    private var colorFillView: ColorFillView? = null
    private var colorButtons: MutableList<Button> = mutableListOf()
    private var templatesRecyclerView: RecyclerView? = null
    private var clearButton: Button? = null
    private var saveButton: ImageView? = null
    private var backButton: ImageView? = null
    private var changeTemplateButton: Button? = null
    private var templateSelectionScreen: LinearLayout? = null
    private var colorFillScreen: LinearLayout? = null

    private val templates = listOf(
        R.drawable.templete_turtle,
        R.drawable.templete_dolphin,
        R.drawable.templete_disney,
        R.drawable.templete_unicorn,
        R.drawable.templete_flower,
        R.drawable.templete_plant,
        R.drawable.templete_duck,
        R.drawable.templete_icecream,
        R.drawable.templete_house,
        R.drawable.templete_fish
    )

    private val colors = listOf(
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
        Color.parseColor("#800080"),
        Color.parseColor("#FFA500"),
        Color.parseColor("#FFC0CB"),
        Color.CYAN,
        Color.parseColor("#8B4513"),
        Color.parseColor("#FFD700"),
        Color.parseColor("#00FF7F"),
        Color.parseColor("#FF1493"),
        Color.parseColor("#4169E1"),
        Color.parseColor("#FF6347"),
        Color.parseColor("#9932CC"),
        Color.parseColor("#32CD32")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_color_fill)

            if (!initializeViews()) {
                Log.e("ColorFillActivity", "Failed to initialize views")
                Toast.makeText(this, "Failed to load activity", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            setupToolbar()
            setupColorPalette()
            setupTemplateSelector()
            setupControls()
            showTemplateSelection()
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews(): Boolean {
        return try {
            colorFillView = findViewById(R.id.color_fill_view)
            templatesRecyclerView = findViewById(R.id.templates_recycler_view)
            clearButton = findViewById(R.id.clear_button)
            saveButton = findViewById(R.id.btn_save)
            backButton = findViewById(R.id.btn_back)
            changeTemplateButton = findViewById(R.id.btn_change_template)
            templateSelectionScreen = findViewById(R.id.template_selection_screen)
            colorFillScreen = findViewById(R.id.color_fill_screen)

            if (colorFillView == null || templatesRecyclerView == null ||
                clearButton == null || saveButton == null || backButton == null ||
                changeTemplateButton == null || templateSelectionScreen == null ||
                colorFillScreen == null) {
                Log.e("ColorFillActivity", "One or more required views are null")
                return false
            }

            val buttonIds = listOf(
                R.id.color_red, R.id.color_blue, R.id.color_green, R.id.color_yellow,
                R.id.color_purple, R.id.color_orange, R.id.color_pink, R.id.color_cyan,
                R.id.color_brown, R.id.color_gold, R.id.color_spring_green, R.id.color_deep_pink,
                R.id.color_royal_blue, R.id.color_tomato, R.id.color_dark_orchid, R.id.color_lime_green
            )

            colorButtons.clear()
            for (id in buttonIds) {
                try {
                    val button: Button? = findViewById(id)
                    if (button != null) {
                        colorButtons.add(button)
                    } else {
                        Log.w("ColorFillActivity", "Button with id $id not found")
                    }
                } catch (e: Exception) {
                    Log.e("ColorFillActivity", "Error finding button", e)
                }
            }

            if (colorButtons.isEmpty()) {
                Log.e("ColorFillActivity", "No color buttons found")
                return false
            }

            true
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error initializing views", e)
            false
        }
    }

    private fun setupToolbar() {
        try {
            backButton?.setOnClickListener {
                try {
                    if (colorFillScreen?.visibility == View.VISIBLE) {
                        showTemplateSelection()
                    } else {
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("ColorFillActivity", "Error in back button", e)
                    finish()
                }
            }

            saveButton?.setOnClickListener {
                try {
                    saveColoringToGallery()
                } catch (e: Exception) {
                    Log.e("ColorFillActivity", "Error in save button", e)
                    Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error setting up toolbar", e)
        }
    }

    private fun setupColorPalette() {
        try {
            colorButtons.forEachIndexed { index, button ->
                if (index < colors.size) {
                    try {
                        applyColorSwatch(button, colors[index], selected = index == 0)
                        button.setOnClickListener {
                            selectColor(colors[index], button)
                        }
                    } catch (e: Exception) {
                        Log.e("ColorFillActivity", "Error setting up color button", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error in setupColorPalette", e)
        }
    }

    private fun setupTemplateSelector() {
        try {
            templatesRecyclerView?.let { recyclerView ->
                recyclerView.layoutManager = GridLayoutManager(this, 2)
                recyclerView.adapter = TemplateAdapter(templates) { templateResId ->
                    loadTemplateAndShowColorFill(templateResId)
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error setting up template selector", e)
        }
    }

    private fun setupControls() {
        try {
            clearButton?.setOnClickListener {
                try {
                    colorFillView?.clearCanvas()
                    animateButton(it)
                } catch (e: Exception) {
                    Log.e("ColorFillActivity", "Error in clear button", e)
                }
            }

            changeTemplateButton?.setOnClickListener {
                try {
                    showTemplateSelection()
                } catch (e: Exception) {
                    Log.e("ColorFillActivity", "Error in change template", e)
                }
            }
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error setting up controls", e)
        }
    }

    private fun showTemplateSelection() {
        try {
            templateSelectionScreen?.visibility = View.VISIBLE
            colorFillScreen?.visibility = View.GONE
            animateTemplateSelection()
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error showing template selection", e)
        }
    }

    private fun showColorFillScreen() {
        try {
            templateSelectionScreen?.visibility = View.GONE
            colorFillScreen?.visibility = View.VISIBLE
            animateColorFillInterface()
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error showing color fill screen", e)
        }
    }

    private fun loadTemplateAndShowColorFill(templateResId: Int) {
        try {
            colorFillView?.loadTemplate(templateResId)
            showColorFillScreen()
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error loading template", e)
            Toast.makeText(this, "Failed to load template", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectColor(color: Int, button: Button) {
        try {
            colorFillView?.setFillColor(color)
            colorButtons.forEachIndexed { index, swatch ->
                applyColorSwatch(swatch, colors[index], selected = swatch == button)
            }
            colorButtons.forEach { it.clearAnimation() }
            animateButton(button)
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error selecting color", e)
        }
    }

    private fun saveColoringToGallery() {
        try {
            val bitmap = colorFillView?.saveColoring()
            if (bitmap != null && !bitmap.isRecycled) {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename = "coloring_$timestamp.png"

                val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveImageToGalleryAPI29Plus(bitmap, filename)
                } else {
                    saveImageToGalleryLegacy(bitmap, filename)
                }

                if (success) {
                    Toast.makeText(this, "Saved to Gallery!", Toast.LENGTH_LONG).show()
                    saveButton?.let { animateButton(it) }
                } else {
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Nothing to save!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error saving to gallery", e)
            Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToGalleryAPI29Plus(bitmap: Bitmap, filename: String): Boolean {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/ColorFill")
            }

            val resolver = contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    true
                } ?: false
            } ?: false
        } catch (e: IOException) {
            Log.e("ColorFillActivity", "Error saving API 29+", e)
            false
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error saving API 29+", e)
            false
        }
    }

    private fun saveImageToGalleryLegacy(bitmap: Bitmap, filename: String): Boolean {
        return try {
            val savedImageURL = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                filename,
                "Color Fill artwork"
            )
            !savedImageURL.isNullOrEmpty()
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error saving legacy", e)
            false
        }
    }

    private fun animateTemplateSelection() {
        try {
            val fadeInAnimation = android.view.animation.AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            templatesRecyclerView?.startAnimation(fadeInAnimation)
        } catch (e: Exception) {
            Log.w("ColorFillActivity", "Animation failed", e)
        }
    }

    private fun animateColorFillInterface() {
        try {
            val fadeInAnimation = android.view.animation.AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            colorButtons.forEachIndexed { index, button ->
                button.postDelayed({
                    try {
                        button.startAnimation(fadeInAnimation)
                    } catch (e: Exception) {
                        Log.w("ColorFillActivity", "Button animation failed", e)
                    }
                }, index * 20L)
            }

            val slideUpAnimation = android.view.animation.AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
            clearButton?.postDelayed({
                try {
                    clearButton?.startAnimation(slideUpAnimation)
                } catch (e: Exception) {
                    Log.w("ColorFillActivity", "Clear animation failed", e)
                }
            }, 400L)

            changeTemplateButton?.postDelayed({
                try {
                    changeTemplateButton?.startAnimation(slideUpAnimation)
                } catch (e: Exception) {
                    Log.w("ColorFillActivity", "Change template animation failed", e)
                }
            }, 500L)
        } catch (e: Exception) {
            Log.w("ColorFillActivity", "Interface animation failed", e)
        }
    }

    private fun animateButton(view: View) {
        try {
            val scaleUp = android.view.animation.ScaleAnimation(
                1f, 1.2f, 1f, 1.2f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 100
                repeatCount = 1
                repeatMode = android.view.animation.Animation.REVERSE
            }
            view.startAnimation(scaleUp)
        } catch (e: Exception) {
            Log.w("ColorFillActivity", "Button animation failed", e)
        }
    }

    private fun applyColorSwatch(button: Button, color: Int, selected: Boolean) {
        try {
            val strokeColor = if (selected) Color.WHITE else Color.parseColor("#66000000")
            val strokeWidth = if (selected) dpToPx(2) else dpToPx(1)
            val swatch = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
                setStroke(strokeWidth, strokeColor)
            }
            button.backgroundTintList = null
            button.background = swatch
            button.text = ""
            button.minWidth = 0
            button.minHeight = 0
            button.setPadding(0, 0, 0, 0)
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error applying swatch", e)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            colorFillView?.clearCanvas()
            colorFillView = null
            colorButtons.clear()
            templatesRecyclerView = null
        } catch (e: Exception) {
            Log.e("ColorFillActivity", "Error in onDestroy", e)
        }
    }
}

class TemplateAdapter(
    private val templates: List<Int>,
    private val onTemplateClick: (Int) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

    inner class TemplateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val templateImage: ImageView = view.findViewById(R.id.template_image)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TemplateViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.templete_item, parent, false)
        return TemplateViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val templateResId = templates[position]

        try {
            val bitmap = BitmapFactory.decodeResource(
                holder.itemView.context.resources,
                templateResId,
                BitmapFactory.Options().apply {
                    inSampleSize = 4
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
            ) ?: TemplateSketchFactory.createTemplate(templateResId, 320, 320)

            val croppedHeight = (bitmap.height * 0.90).toInt()
            if (croppedHeight > 0 && croppedHeight <= bitmap.height) {
                val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, croppedHeight)
                if (bitmap != croppedBitmap) bitmap.recycle()
                holder.templateImage.setImageBitmap(croppedBitmap)
            } else {
                holder.templateImage.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            Log.e("TemplateAdapter", "Error loading template", e)
            holder.templateImage.setImageBitmap(TemplateSketchFactory.createTemplate(templateResId, 320, 320))
        }

        holder.templateImage.setOnClickListener {
            try {
                onTemplateClick(templateResId)
                val scaleUp = android.view.animation.ScaleAnimation(
                    1f, 1.1f, 1f, 1.1f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    duration = 100
                    repeatCount = 1
                    repeatMode = android.view.animation.Animation.REVERSE
                }
                holder.templateImage.startAnimation(scaleUp)
            } catch (e: Exception) {
                Log.e("TemplateAdapter", "Error in click", e)
                onTemplateClick(templateResId)
            }
        }
    }

    override fun getItemCount() = templates.size
}
