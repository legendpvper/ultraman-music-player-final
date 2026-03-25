package com.ultraman.themes.model

data class UltramanCharacter(
    val name: String,
    val series: String,
    val year: Int,
    val color: String = "#1565C0",
    val songs: List<UltramanSong>
)