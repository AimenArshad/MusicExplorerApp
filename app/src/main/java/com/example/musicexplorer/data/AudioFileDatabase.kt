package com.example.musicexplorer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AudioFile::class], version = 1)
abstract class AudioFileDatabase: RoomDatabase(){
    abstract fun audioFileDao(): AudioFileDao

    companion object {
        @Volatile
        private var INSTANCE: AudioFileDatabase? = null

        fun getDatabase(context: Context): AudioFileDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AudioFileDatabase::class.java,
                    "audio_file_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }

}