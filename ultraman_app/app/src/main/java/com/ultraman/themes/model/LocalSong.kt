package com.ultraman.themes.model

import java.io.Serializable

data class LocalSong(
    val id: String,          // unique ID (we'll use the URI string)
    val title: String,
    val artist: String,
    val uri: String          // the file URI on the phone
) : Serializable