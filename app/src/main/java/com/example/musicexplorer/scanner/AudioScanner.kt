package com.example.musicexplorer.scanner

import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Log
import com.example.musicexplorer.data.AudioFile
import com.example.musicexplorer.data.AudioFileDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object AudioScanner {
    suspend fun scanAndStoreAudioFiles(context: Context): List<AudioFile> {
        return withContext(Dispatchers.IO) {
            val audioList = mutableListOf<AudioFile>()

            val audios = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val content = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val result = context.contentResolver.query(audios, content, selection, null, null)

            result?.use {
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                while (it.moveToNext()) {
                    val title = it.getString(titleCol)
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val duration = it.getLong(durationCol)
                    val path = it.getString(pathCol)
                    val size = it.getLong(sizeColumn)

                    val audioFile = AudioFile(
                        title = title,
                        artist = artist,
                        duration = duration,
                        path = path,
                        size = size,
                        albumArt = extractEmbeddedAlbumArt(path)
                    )
                    Log.d("aimen","here4")
                    audioList.add(audioFile)
                }
            }

            Log.d("aimen","here5")
            audioList
        }
    }
    private fun extractEmbeddedAlbumArt(filePath: String): ByteArray? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val art = retriever.embeddedPicture
            retriever.release()
            art
        } catch (e: Exception) {
            Log.e("AlbumArt", "Error reading album art for $filePath: ${e.message}")
            null
        }
    }

}