package com.example.musicexplorer.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicexplorer.data.AudioFileDatabase
import com.example.musicexplorer.data.AudioFileRepository

class AudioViewModelFactory(private val audioFileRepository: AudioFileRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioFileViewModel::class.java)) {
            return AudioFileViewModel(audioFileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}