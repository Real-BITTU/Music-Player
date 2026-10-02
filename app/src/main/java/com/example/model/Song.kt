package com.example.model

data class Song(
  val id: String,
  val title: String,
  val artist: String,
  val album: String,
  val durationMs: Long,
  val audioUrl: String,
  val albumArtUrl: String,
  val genre: String,
  val lyrics: List<String> = emptyList()
)

enum class RepeatMode {
  OFF,
  ALL,
  ONE
}

enum class FullPlayerTab {
  NOW_PLAYING,
  QUEUE,
  LYRICS
}
