package com.example.musicexplorer.utils

import android.content.Context

object Prefs {

    private const val NAME = "app_prefs"
    private const val KEY_FILE_OPEN_COUNT = "file_open_count"

    fun incrementFileOpenCount(context: Context): Int {
        val prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_FILE_OPEN_COUNT, 0) + 1
        prefs.edit().putInt(KEY_FILE_OPEN_COUNT, count).apply()
        return count
    }

    fun resetFileOpenCount(context: Context) {
        val prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_FILE_OPEN_COUNT, 0).apply()
    }

    fun getCount(context: Context): Int {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getInt(KEY_FILE_OPEN_COUNT, 0)
    }
}
