package com.example.musicexplorer.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicexplorer.data.AudioFileDatabase
import com.example.musicexplorer.data.AudioFileRepository

class AudioViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = AudioFileDatabase.getDatabase(context).audioFileDao()
        val repository = AudioFileRepository(dao)
        return AudioFileViewModel(repository) as T
    }
}