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

            val audios = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI   // uri for the audios in our external storage
            val content = arrayOf(            // array of columns we want to retrieve from our audios
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE
            )

            val result = context.contentResolver.query(audios, content, null, null, null) //querying from content provideer

            result?.use {        // use the result of query
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)              // get index of all the fields in audio file
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                while (it.moveToNext()) {                               // get actual data from the index
                    val title = it.getString(titleCol)
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val duration = it.getLong(durationCol)
                    val path = it.getString(pathCol)
                    val size = it.getLong(sizeColumn)

                    val audioFile = AudioFile(               //create object of AudioFile
                        title = title,
                        artist = artist,
                        duration = duration,
                        path = path,
                        size = size,
                        albumArt = extractEmbeddedAlbumArt(path)
                    )
                    audioList.add(audioFile)
                }
            }
            audioList                 //return list of all audio files
        }
    }
    private fun extractEmbeddedAlbumArt(filePath: String): ByteArray? {
        return try {
            val retriever = MediaMetadataRetriever()         //retrieve meta data of file
            retriever.setDataSource(filePath)               // file path to receive meta data from
            val art = retriever.embeddedPicture             // get picture from meta data
            retriever.release()                            // free up resource
            art                           // return picture
        } catch (e: Exception) {
            Log.e("AlbumArt", "Error reading album art for $filePath: ${e.message}")
            null
        }
    }

}