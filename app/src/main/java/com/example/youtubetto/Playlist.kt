package com.example.youtubetto
import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val kind: String,
    val etag: String,
    val items: List<PlaylistItem> = listOf(),
)

