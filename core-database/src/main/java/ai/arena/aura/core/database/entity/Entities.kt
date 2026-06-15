package ai.arena.aura.core.database.entity

import androidx.room.*

@Entity(tableName = "songs", indices = [Index("title"), Index("artistId"), Index("albumId"), Index("genre"), Index("format"), Index("dateAdded"), Index("lastPlayedAt"), Index("playCount"), Index("isFavorite"), Index("isDownloaded"), Index("folderPath")])
data class SongEntity(
    @PrimaryKey val id: String,
    val uri: String,
    val title: String,
    val sortTitle: String,
    val artistId: String?,
    val artist: String,
    val albumId: String?,
    val album: String,
    val albumArtist: String?,
    val genre: String,
    val composer: String?,
    val trackNumber: Int,
    val discNumber: Int,
    val durationMs: Long,
    val format: String,
    val mimeType: String?,
    val sampleRate: Int?,
    val bitDepth: Int?,
    val bitrateKbps: Int?,
    val channelCount: Int?,
    val fileSizeBytes: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val lastPlayedAt: Long?,
    val playCount: Int,
    val rating: Int,
    val isFavorite: Boolean,
    val isDownloaded: Boolean,
    val folderPath: String?,
    val artworkUri: String?,
    val dominantColor: Long?,
    val accentColor: Long?,
    val replayGainTrack: Float?,
    val replayGainAlbum: Float?,
    val lyricsId: String?
)

@Entity(tableName = "albums", indices = [Index("title"), Index("artist"), Index("releaseYear"), Index("isFavorite")])
data class AlbumEntity(@PrimaryKey val id: String, val title: String, val artist: String, val releaseYear: Int?, val genre: String?, val artworkUri: String?, val songCount: Int, val totalDurationMs: Long, val isFavorite: Boolean)

@Entity(tableName = "artists", indices = [Index("name"), Index("isFavorite")])
data class ArtistEntity(@PrimaryKey val id: String, val name: String, val biography: String?, val artworkUri: String?, val songCount: Int, val albumCount: Int, val isFavorite: Boolean)

@Entity(tableName = "genres", indices = [Index("name")])
data class GenreEntity(@PrimaryKey val id: String, val name: String, val songCount: Int, val albumCount: Int)

@Entity(tableName = "playlists", indices = [Index("title"), Index("isPinned")])
data class PlaylistEntity(@PrimaryKey val id: String, val title: String, val description: String, val type: String, val artworkUri: String?, val dateCreated: Long, val dateModified: Long, val isPinned: Boolean, val isSmart: Boolean, val songCount: Int, val totalDurationMs: Long)

@Entity(tableName = "playlist_songs", primaryKeys = ["playlistId", "songId"], indices = [Index("playlistId"), Index("songId"), Index("position")])
data class PlaylistSongEntity(val playlistId: String, val songId: String, val position: Int, val dateAdded: Long)

@Entity(tableName = "favorites")
data class FavoriteEntity(@PrimaryKey val songId: String, val createdAt: Long, val source: String)

@Entity(tableName = "downloads", indices = [Index("songId"), Index("status")])
data class DownloadEntity(@PrimaryKey val id: String, val songId: String?, val remoteUrl: String?, val localUri: String, val status: String, val progress: Int, val bytesDownloaded: Long, val totalBytes: Long, val createdAt: Long, val completedAt: Long?, val errorMessage: String?)

@Entity(tableName = "history", indices = [Index("songId"), Index("playedAt"), Index("completed")])
data class HistoryEntity(@PrimaryKey val id: String, val songId: String, val title: String, val artist: String, val album: String, val genre: String, val format: String, val playedAt: Long, val durationPlayedMs: Long, val completed: Boolean, val artworkUri: String?)

@Entity(tableName = "queue", indices = [Index("position"), Index("isCurrent")])
data class QueueEntity(@PrimaryKey val id: String, val songId: String, val position: Int, val source: String, val addedAt: Long, val isCurrent: Boolean)

@Entity(tableName = "settings")
data class SettingEntity(@PrimaryKey val key: String, val value: String, val updatedAt: Long)

@Entity(tableName = "lyrics", indices = [Index("songId")])
data class LyricsEntity(@PrimaryKey val id: String, val songId: String, val type: String, val language: String?, val source: String, val rawText: String, val syncedJson: String?, val createdAt: Long, val updatedAt: Long)

@Entity(tableName = "folders", indices = [Index("path", unique = true)])
data class FolderEntity(@PrimaryKey val id: String, val path: String, val displayName: String, val treeUri: String?, val isWatched: Boolean, val lastScannedAt: Long?, val songCount: Int)

@Fts4
@Entity(tableName = "songs_fts")
data class SongFtsEntity(val title: String, val artist: String, val album: String, val genre: String, val composer: String?, val lyricsText: String?)
