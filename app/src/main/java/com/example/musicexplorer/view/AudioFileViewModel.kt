package com.example.musicexplorer.view


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicexplorer.data.AudioFile
import com.example.musicexplorer.data.AudioFileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AudioFileViewModel(
    private val repository: AudioFileRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val allAudioFilesFlow: Flow<List<AudioFile>> =
        repository.getAllAudioFiles()

    val filteredAudioFiles: Flow<List<AudioFile>> =
        combine(allAudioFilesFlow, _searchQuery) { audioList, query ->
            if (query.isBlank()) {
                audioList
            } else {
                audioList.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }
        }
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun scanAndStoreAudioFiles(context: Context) {
        viewModelScope.launch {
            repository.storeAudioFiles(context)
        }
    }
}
