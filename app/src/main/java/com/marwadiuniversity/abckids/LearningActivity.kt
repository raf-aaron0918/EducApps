package com.marwadiuniversity.abckids

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
            VideoItem("Vowels Song", R.raw.vowels_song, "Learning A E I O U"),
            VideoItem("Shhsmscey Tv", R.raw.shsmcey_tv, "Fun Song"),
            VideoItem("Pepe Ay Pribado", R.raw.pepe_ay_pribado_trim, "Nursery Rhyme")
        )

        val badge = findViewById<TextView>(R.id.video_count_badge)
        badge.text = "${videos.size} Videos"

        val recyclerView = findViewById<RecyclerView>(R.id.video_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = VideoAdapter(videos) { video ->
val intent = Intent(this, VideoPlayerActivity::class.java)
             intent.putExtra("VIDEO_RES_ID", video.resId)
             startActivity(intent)
        }
    }
}