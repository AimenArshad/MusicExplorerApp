package com.example.musicexplorer.data

import android.content.Context
import android.util.Log
import com.example.musicexplorer.scanner.AudioScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AudioFileRepository(private val audioFileDao: AudioFileDao) {
    fun getAllAudioFiles(): Flow<List<AudioFile>> {
        return audioFileDao.getAllAudioFiles()
    }
    suspend fun storeAudioFiles(context: Context){
        val scannedList = AudioScanner.scanAndStoreAudioFiles(context)
        audioFileDao.clearAll()
        audioFileDao.insertAll(scannedList)
    }
}