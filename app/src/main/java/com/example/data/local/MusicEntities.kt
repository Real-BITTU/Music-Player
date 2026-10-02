package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
  @PrimaryKey val songId: String,
  val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "play_history")
data class PlayHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val songId: String,
  val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "last_playback")
data class PlaybackStateEntity(
  @PrimaryKey val id: Int = 1,
  val lastSongId: String,
  val lastPositionMs: Long = 0L,
  val updatedAt: Long = System.currentTimeMillis()
)
