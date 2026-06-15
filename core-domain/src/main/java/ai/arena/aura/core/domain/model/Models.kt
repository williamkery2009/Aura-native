package ai.arena.aura.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val uri: String,
    val title: String,
    val sortTitle: String = title,
    val artistId: String? = null,
    val artist: String = "Unknown Performer",
    val albumId: String? = null,
    val album: String = "Private Offline Collection",
    val albumArtist: String? = null,
    val genre: String = "Lossless Vault",
    val composer: String? = null,
    val trackNumber: Int = 0,
    val discNumber: Int = 0,
    val durationMs: Long = 0,
    val format: String = "UNKNOWN",
    val mimeType: String? = null,
    val sampleRate: Int? = null,
    val bitDepth: Int? = null,
    val bitrateKbps: Int? = null,
    val channelCount: Int? = null,
    val fileSizeBytes: Long = 0,
    val dateAdded: Long = 0,
    val dateModified: Long = 0,
    val lastPlayedAt: Long? = null,
    val playCount: Int = 0,
    val rating: Int = 0,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val folderPath: String? = null,
    val artworkUri: String? = null,
    val dominantColor: Long? = null,
    val accentColor: Long? = null,
    val replayGainTrack: Float? = null,
    val replayGainAlbum: Float? = null,
    val lyricsId: String? = null
)

@Serializable
data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val releaseYear: Int? = null,
    val genre: String? = null,
    val artworkUri: String? = null,
    val songCount: Int = 0,
    val totalDurationMs: Long = 0,
    val isFavorite: Boolean = false
)

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val biography: String? = null,
    val artworkUri: String? = null,
    val songCount: Int = 0,
    val albumCount: Int = 0,
    val isFavorite: Boolean = false
)

@Serializable
data class Genre(val id: String, val name: String, val songCount: Int = 0, val albumCount: Int = 0)

@Serializable
data class Playlist(
    val id: String,
    val title: String,
    val description: String = "Curated private offline audio execution list.",
    val type: PlaylistType = PlaylistType.Manual,
    val artworkUri: String? = null,
    val dateCreated: Long = 0,
    val dateModified: Long = 0,
    val isPinned: Boolean = false,
    val isSmart: Boolean = false,
    val songCount: Int = 0,
    val totalDurationMs: Long = 0
)

@Serializable
enum class PlaylistType { Manual, Smart, Imported, Favorites, RecentlyPlayed, MostPlayed, Downloads }

@Serializable
data class QueueItem(val id: String, val song: Song, val position: Int, val source: String, val addedAt: Long)

@Serializable
enum class RepeatMode { Off, All, One }

@Serializable
data class AuraPlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val bufferedPositionMs: Long = 0,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.Off,
    val speed: Float = 1f,
    val queue: List<Song> = emptyList()
)

@Serializable
data class LyricsLine(
    val startTimeMs: Long,
    val endTimeMs: Long,
    val text: String,
    val words: List<LyricsWord> = emptyList(),
    val translation: String? = null,
    val romanization: String? = null
)

@Serializable
data class LyricsWord(val text: String, val startTimeMs: Long, val durationMs: Long)

@Serializable
data class Lyrics(
    val id: String,
    val songId: String,
    val type: LyricsType,
    val language: String? = null,
    val rawText: String,
    val lines: List<LyricsLine> = emptyList()
)

@Serializable
enum class LyricsType { Unsynced, LineSynced, WordSynced, EnhancedSync }

@Serializable
data class AuraSettings(
    val theme: AuraThemeMode = AuraThemeMode.DarkLiquidGlass,
    val accent: AuraAccent = AuraAccent.CyberpunkCyan,
    val crossfadeSeconds: Int = 0,
    val gaplessEnabled: Boolean = true,
    val replayGainMode: ReplayGainMode = ReplayGainMode.Off,
    val highContrast: Boolean = false,
    val reducedMotion: Boolean = false
)

@Serializable
enum class AuraThemeMode { DarkLiquidGlass, TrueBlackOled, LightStudioGlass, System }
@Serializable
enum class AuraAccent { CyberpunkCyan, AppleCrimson, AudiophileGold, MaterialYou }
@Serializable
enum class ReplayGainMode { Album, Track, Off }

@Serializable
data class ReplaySummary(
    val totalListeningMs: Long = 0,
    val totalPlays: Int = 0,
    val hiResPlayCount: Int = 0,
    val topGenres: List<Pair<String, Long>> = emptyList(),
    val topArtists: List<Pair<String, Int>> = emptyList(),
    val topSongs: List<Pair<String, Int>> = emptyList(),
    val longestStreakDays: Int = 0
)
