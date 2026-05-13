package com.marwadiuniversity.abckids.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marwadiuniversity.abckids.R

data class VideoItem(
    val title: String,
    val resId: Int,
    val description: String = "Nursery Rhyme"
)

class VideoAdapter(
    private val videos: List<VideoItem>,
    private val onVideoClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.video_title)
        val descText: TextView = view.findViewById(R.id.video_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.titleText.text = video.title
        holder.descText.text = video.description

        holder.itemView.setOnClickListener {
            onVideoClick(video)
        }
    }

    override fun getItemCount() = videos.size
}
