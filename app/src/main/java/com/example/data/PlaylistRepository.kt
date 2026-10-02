package com.example.data

import com.example.model.Song

object PlaylistRepository {

  /**
   * Hardcoded playlist of 5 songs.
   * Audio stream URLs are set to verified public test streams and can be easily
   * replaced with custom stream URLs whenever needed.
   */
  val defaultPlaylist: List<Song> = listOf(
    Song(
      id = "song_1",
      title = "Midnight Horizon",
      artist = "Synthetica Waves",
      album = "Neon Dreams EP",
      durationMs = 215_000L,
      audioUrl = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
      albumArtUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
      genre = "Synthwave",
      lyrics = listOf(
        "Neon lights across the skyline",
        "Driving through the cyber highway",
        "Echoes of an analog dream",
        "Lost within the digital stream",
        "Midnight comes and takes control",
        "Electric pulse inside my soul",
        "The city glows with purple haze",
        "Wandering through a sonic maze"
      )
    ),
    Song(
      id = "song_2",
      title = "Starlight Reverie",
      artist = "Lofi Odyssey",
      album = "Quiet Hours",
      durationMs = 372_000L,
      audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
      albumArtUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
      genre = "Lofi Chill",
      lyrics = listOf(
        "Raindrops hitting on the window pane",
        "Warm tea and nostalgic memories",
        "Time is moving slow tonight",
        "Bathed under the soft lamp light",
        "Lofi beats to drift away",
        "Until the rise of another day",
        "Peace of mind in gentle tone",
        "Never feeling quite alone"
      )
    ),
    Song(
      id = "song_3",
      title = "Cybernetic Pulse",
      artist = "Astral Circuit",
      album = "Quantum Drift",
      durationMs = 423_000L,
      audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
      albumArtUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800&auto=format&fit=crop&q=80",
      genre = "Electronic",
      lyrics = listOf(
        "Synthesizer frequency high",
        "Bassline thundering in the sky",
        "Data flowing in the wire",
        "Sparks ignited like a fire",
        "Dance beneath the laser beam",
        "Living in a hyper dream",
        "Future rhythm taking flight",
        "Shining in the strobe of night"
      )
    ),
    Song(
      id = "song_4",
      title = "Golden Hour Glow",
      artist = "Solstice Trio",
      album = "Velvet Shore",
      durationMs = 345_000L,
      audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
      albumArtUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&auto=format&fit=crop&q=80",
      genre = "Acoustic Pop",
      lyrics = listOf(
        "Sun sinks low beneath the sea",
        "Golden rays are washing over me",
        "A gentle breeze upon the shore",
        "Couldn't ask for anything more",
        "Warm melodies and summer skies",
        "Catch the spark inside your eyes",
        "Hold this fleeting moment near",
        "Whispers that are crystal clear"
      )
    ),
    Song(
      id = "song_5",
      title = "Echoes of Silence",
      artist = "Deep Aurora",
      album = "Infinite Cosmos",
      durationMs = 310_000L,
      audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
      albumArtUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=800&auto=format&fit=crop&q=80",
      genre = "Ambient",
      lyrics = listOf(
        "Floating into the deep unknown",
        "Across galaxies unseen, alone",
        "Harmonics of the stellar space",
        "Suspended in a timeless place",
        "Breathe the celestial air",
        "Quiet wonder everywhere",
        "Echoes fading out of sight",
        "Entering the eternal light"
      )
    )
  )

  val genreFilters: List<String> = listOf("All", "Synthwave", "Lofi Chill", "Electronic", "Acoustic Pop", "Ambient")
}
