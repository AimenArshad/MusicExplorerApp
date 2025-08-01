package com.example.musicexplorer.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "audio_files")
data class AudioFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String?,
    val duration: Long,
    val size: Long,
    val path: String,
    val albumArt: ByteArray?       //binary data for image file
) : Parcelable

