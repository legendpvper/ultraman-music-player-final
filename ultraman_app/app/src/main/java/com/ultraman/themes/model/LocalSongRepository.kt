package com.ultraman.themes.model

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object LocalSongRepository {

    private const val PREFS_NAME = "local_songs_prefs"
    private const val KEY_SONGS   = "local_songs"

    fun getSongs(context: Context): MutableList<LocalSong> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json  = prefs.getString(KEY_SONGS, "[]") ?: "[]"
        val arr   = JSONArray(json)
        val list  = mutableListOf<LocalSong>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(LocalSong(
                id     = obj.getString("id"),
                title  = obj.getString("title"),
                artist = obj.getString("artist"),
                uri    = obj.getString("uri")
            ))
        }
        return list
    }

    fun addSong(context: Context, song: LocalSong) {
        val list = getSongs(context)
        if (list.none { it.id == song.id }) {
            list.add(song)
            saveSongs(context, list)
        }
    }

    fun removeSong(context: Context, songId: String) {
        val list = getSongs(context).filter { it.id != songId }
        saveSongs(context, list)
    }

    private fun saveSongs(context: Context, songs: List<LocalSong>) {
        val arr = JSONArray()
        songs.forEach { song ->
            val obj = JSONObject()
            obj.put("id",     song.id)
            obj.put("title",  song.title)
            obj.put("artist", song.artist)
            obj.put("uri",    song.uri)
            arr.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_SONGS, arr.toString()).apply()
    }
}