package com.marwadiuniversity.abckids

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoResId = intent.getIntExtra("VIDEO_RES_ID", 0)
        if (videoResId == 0) {
            finish()
            return
        }

        val videoView = findViewById<VideoView>(R.id.video_view)
        val btnBack = findViewById<ImageView>(R.id.btn_back)

        btnBack.setOnClickListener { finish() }

        val videoUri = Uri.parse("android.resource://$packageName/$videoResId")
        videoView.setVideoURI(videoUri)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = false
            videoView.start()
        }

        videoView.setOnCompletionListener {
            finish()
        }
    }
}
