package com.example.musicexplorer.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicexplorer.data.AudioFile
import com.example.musicexplorer.databinding.AudioItemBinding
import com.example.musicexplorer.utils.FileUtils


class AudioFileAdapter(
    private val onItemClick: (AudioFile) -> Unit
) : ListAdapter<AudioFile, AudioFileAdapter.AudioViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val binding = AudioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AudioViewHolder(
        private val binding: AudioItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(audio: AudioFile) = with(binding) {
            fileName.text = audio.title
            artistName.text = audio.artist
            duration.text = FileUtils.formatDuration(audio.duration)
            root.setOnClickListener { onItemClick(audio) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AudioFile>() {
            override fun areItemsTheSame(oldItem: AudioFile, newItem: AudioFile): Boolean =
                oldItem.path == newItem.path

            override fun areContentsTheSame(oldItem: AudioFile, newItem: AudioFile): Boolean =
                oldItem == newItem
        }

        fun formatDuration(duration: Long): String {
            val minutes = (duration / 1000) / 60
            val seconds = (duration / 1000) % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }



}
