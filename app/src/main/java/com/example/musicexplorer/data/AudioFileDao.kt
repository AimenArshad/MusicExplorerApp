package com.example.musicexplorer.data

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioFileDao {
    @Query("SELECT * FROM audio_files")
    fun getAllAudioFiles(): Flow<List<AudioFile>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(audioFiles: List<AudioFile>)
    @Query("DELETE FROM audio_files")
    suspend fun clearAll()
}