package com.marwadiuniversity.abckids

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeveloperActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var developer1Name: TextView
    private lateinit var developer1Branch: TextView
    private lateinit var developer1Photo: ImageView
    private lateinit var developer2Name: TextView
    private lateinit var developer2Branch: TextView
    private lateinit var developer2Photo: ImageView
    private lateinit var developer3Name: TextView
    private lateinit var developer3Branch: TextView
    private lateinit var developer3Photo: ImageView
    private lateinit var developer4Name: TextView
    private lateinit var developer4Branch: TextView
    private lateinit var developer4Photo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        initializeViews()
        setupDeveloperInfo()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back)
        developer1Name = findViewById(R.id.developer1_name)
        developer1Branch = findViewById(R.id.developer1_branch)
        developer1Photo = findViewById(R.id.developer1_photo)
        developer2Name = findViewById(R.id.developer2_name)
        developer2Branch = findViewById(R.id.developer2_branch)
        developer2Photo = findViewById(R.id.developer2_photo)
        developer3Name = findViewById(R.id.developer3_name)
        developer3Branch = findViewById(R.id.developer3_branch)
        developer3Photo = findViewById(R.id.developer3_photo)
        developer4Name = findViewById(R.id.developer4_name)
        developer4Branch = findViewById(R.id.developer4_branch)
        developer4Photo = findViewById(R.id.developer4_photo)
    }

    private fun setupDeveloperInfo() {
        developer1Name.text = "Rafael Aaron Dela Rosa"
        developer1Branch.text = "BS Computer Science"

        developer2Name.text = "John Patrick Mendoza"
        developer2Branch.text = "BS Computer Science"

        developer3Name.text = "Klein Silvan"
        developer3Branch.text = "BS Computer Science"

        developer4Name.text = "Jo Mari Esguerra"
        developer4Branch.text = "BS Computer Science"
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        developer1Photo.setOnClickListener {
            showImagePopup(R.drawable.dela_rosa)
        }

        developer2Photo.setOnClickListener {
            showImagePopup(R.drawable.mendoza)
        }

        developer3Photo.setOnClickListener {
            showImagePopup(R.drawable.silvan)
        }

        developer4Photo.setOnClickListener {
            showImagePopup(R.drawable.esguerra)
        }
    }

    private fun showImagePopup(imageResId: Int) {
        val imageView = ImageView(this)
        imageView.setImageResource(imageResId)
        
        // Set smaller square size and add border
        val size = 200
        imageView.layoutParams = android.widget.LinearLayout.LayoutParams(size, size)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.adjustViewBounds = true
        imageView.setPadding(4, 4, 4, 4)
        imageView.setBackgroundResource(R.drawable.image_border)

        AlertDialog.Builder(this)
            .setView(imageView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}