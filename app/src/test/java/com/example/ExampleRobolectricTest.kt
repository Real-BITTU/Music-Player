package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PlaylistRepository
import com.example.data.local.FavoriteEntity
import com.example.data.local.MusicDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aura Music", appName)
  }

  @Test
  fun `verify playlist has 5 songs with valid stream urls`() {
    val playlist = PlaylistRepository.defaultPlaylist
    assertEquals(5, playlist.size)
    playlist.forEach { song ->
      assertTrue(song.audioUrl.isNotEmpty())
      assertTrue(song.albumArtUrl.isNotEmpty())
      assertTrue(song.title.isNotEmpty())
    }
  }

  @Test
  fun `verify search filters by title and artist`() {
    val playlist = PlaylistRepository.defaultPlaylist

    // Search by title
    val byTitle = playlist.filter { it.title.contains("Midnight", ignoreCase = true) }
    assertEquals(1, byTitle.size)
    assertEquals("Midnight Horizon", byTitle.first().title)

    // Search by artist
    val byArtist = playlist.filter { it.artist.contains("Lofi", ignoreCase = true) }
    assertEquals(1, byArtist.size)
    assertEquals("Starlight Reverie", byArtist.first().title)

    // Search case-insensitive
    val caseInsensitive = playlist.filter { it.title.contains("cyber", ignoreCase = true) }
    assertEquals(1, caseInsensitive.size)
    assertEquals("Cybernetic Pulse", caseInsensitive.first().title)
  }

  @Test
  fun `verify room database persistence for favorites`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = MusicDatabase.getDatabase(context)
    val dao = db.musicDao()

    dao.insertFavorite(FavoriteEntity(songId = "song_test_1"))
    val favorites = dao.getAllFavoriteIds().first()
    assertTrue(favorites.contains("song_test_1"))

    dao.deleteFavorite("song_test_1")
    val afterDelete = dao.getAllFavoriteIds().first()
    assertTrue(!afterDelete.contains("song_test_1"))
  }
}
