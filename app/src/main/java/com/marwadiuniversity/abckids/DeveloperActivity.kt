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
    }

    private fun setupDeveloperInfo() {
        developer1Name.text = "Komal Kumari"
        developer1Branch.text = "B.Tech, Computer Engineering"

        developer2Name.text = "Thun Thingyan Phoo"
        developer2Branch.text = "B.Tech, Computer Engineering"
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        developer1Photo.setOnClickListener {
            showImagePopup(R.drawable.developer1_photo)
        }

        developer2Photo.setOnClickListener {
            showImagePopup(R.drawable.developer2_photo)
        }
    }

    private fun showImagePopup(imageResId: Int) {
        val imageView = ImageView(this)
        imageView.setImageResource(imageResId)
        imageView.layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )

        AlertDialog.Builder(this)
            .setView(imageView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}