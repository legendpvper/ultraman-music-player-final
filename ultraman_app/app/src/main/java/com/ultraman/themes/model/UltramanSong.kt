package com.ultraman.themes.model

import java.io.Serializable

data class UltramanSong(
    val id: Int,
    val title: String,
    val artist: String,
    val series: String,
    val year: Int,
    val assetFileName: String,   // filename inside assets/ folder e.g. "01_ultraman_no_uta.mp3"
    val color: String = "#1565C0"
) : Serializable
