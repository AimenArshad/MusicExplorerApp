package com.example.musicexplorer.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.musicexplorer.R
import com.example.musicexplorer.data.AudioFile
import com.example.musicexplorer.databinding.AudioDetailFragmentBinding

class AudioDetailFragment : Fragment() {

    private var _binding: AudioDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private var audioFile: AudioFile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AudioDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioFile = arguments?.getParcelable("audioFile")

        audioFile?.let { audio ->
            binding.albumTitle.text = audio.title
            binding.albumArtist.text = audio.artist
            binding.albumDuration.text = formatDuration(audio.duration)
            binding.albumSize.text = "${audio.size / (1024 * 1024)} MB"
            binding.albumPath.text=audio.path
            loadAlbumArt(binding.albumArtImage, audio.albumArt)
        }
    }

    private fun loadAlbumArt(imageView: ImageView, albumArt: ByteArray?) {
        if (albumArt != null) {
            Glide.with(imageView.context)
                .asBitmap()
                .load(albumArt)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.cover)
                        .error(R.drawable.cover)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(R.drawable.cover)
                .into(imageView)
        }
    }

    private fun formatDuration(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
