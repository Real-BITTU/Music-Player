package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

  @Query("SELECT songId FROM favorites")
  fun getAllFavoriteIds(): Flow<List<String>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFavorite(favorite: FavoriteEntity)

  @Query("DELETE FROM favorites WHERE songId = :songId")
  suspend fun deleteFavorite(songId: String)

  @Query("SELECT * FROM play_history ORDER BY playedAt DESC LIMIT 20")
  fun getRecentHistory(): Flow<List<PlayHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistory(history: PlayHistoryEntity)

  @Query("SELECT * FROM last_playback WHERE id = 1 LIMIT 1")
  fun getLastPlayback(): Flow<PlaybackStateEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveLastPlayback(playback: PlaybackStateEntity)
}
