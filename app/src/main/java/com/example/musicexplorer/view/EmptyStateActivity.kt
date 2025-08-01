package com.example.musicexplorer.view

import android.os.Bundle
import com.bumptech.glide.Glide
import androidx.appcompat.app.AppCompatActivity
import com.example.musicexplorer.R
import com.example.musicexplorer.databinding.EmptyStateActivityBinding

class EmptyStateActivity: AppCompatActivity() {
    private lateinit var binding: EmptyStateActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EmptyStateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val message = intent.getStringExtra("infoMessage")
        binding.messageView.text = message?:"Nothing available to display"
        Glide.with(this)
            .asGif()
            .load(R.drawable.emoji)
            .into(binding.gifView)
    }
}