package com.ultraman.themes.model

import java.io.Serializable

data class UltramanSong(
    val id: Int,
    val title: String,
    val artist: String,
    val series: String,
    val year: Int,
    val assetFileName: String,
    val color: String = "#1565C0",
    val songType: String = "Opening" // e.g. "Opening", "Ending"
) : Serializable