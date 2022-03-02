package com.example.youtubetto
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistItem(
    val id: String,
    val etag: String,
)