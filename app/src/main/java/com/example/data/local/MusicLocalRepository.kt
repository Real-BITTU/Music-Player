package com.example.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MusicLocalRepository(private val musicDao: MusicDao) {

  val favoriteSongIds: Flow<List<String>> = musicDao.getAllFavoriteIds()
  val recentHistory: Flow<List<PlayHistoryEntity>> = musicDao.getRecentHistory()
  val lastPlayback: Flow<PlaybackStateEntity?> = musicDao.getLastPlayback()

  suspend fun toggleFavorite(songId: String, isCurrentlyFavorite: Boolean) {
    if (isCurrentlyFavorite) {
      musicDao.deleteFavorite(songId)
    } else {
      musicDao.insertFavorite(FavoriteEntity(songId = songId))
    }
  }

  suspend fun recordSongPlayed(songId: String) {
    musicDao.insertHistory(PlayHistoryEntity(songId = songId))
  }

  suspend fun saveLastPlayback(songId: String, positionMs: Long) {
    musicDao.saveLastPlayback(
      PlaybackStateEntity(
        id = 1,
        lastSongId = songId,
        lastPositionMs = positionMs
      )
    )
  }

  suspend fun seedInitialFavoritesIfEmpty(initialIds: Set<String>) {
    val existing = musicDao.getAllFavoriteIds().firstOrNull()
    if (existing.isNullOrEmpty()) {
      initialIds.forEach { id ->
        musicDao.insertFavorite(FavoriteEntity(songId = id))
      }
    }
  }
}
