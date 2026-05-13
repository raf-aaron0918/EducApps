package com.marwadiuniversity.abckids

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marwadiuniversity.abckids.adapters.VideoAdapter
import com.marwadiuniversity.abckids.adapters.VideoItem

class LearningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        setupBackButton()
        setupVideoList()
    }

    private fun setupBackButton() {
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupVideoList() {
        val videos = listOf(
            VideoItem("Baa Baa Black Sheep", R.raw.baa_baa_black_sheep, "The Joy of Sharing!"),
            VideoItem("Old MacDonald", R.raw.old_macdonald, "Had A Farm!"),
            VideoItem("Twinkle Twinkle", R.raw.twinkle_twinkle, "Little Star"),
            VideoItem("If You're Happy", R.raw.if_you_happy, "Clap Your Hands!"),
            VideoItem("Head Shoulders", R.raw.head_shoulders, "Knees and Toes"),
            VideoItem("Vowels Song", R.raw.vowels_song, "Learning A E I O U")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.video_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = VideoAdapter(videos) { video ->
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_RES_ID", video.resId)
            startActivity(intent)
        }
    }
}