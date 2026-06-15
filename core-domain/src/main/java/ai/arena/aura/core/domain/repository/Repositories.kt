package ai.arena.aura.core.domain.repository

import ai.arena.aura.core.domain.model.*
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun observeSongs(): Flow<List<Song>>
    fun observeRecentlyAdded(limit: Int): Flow<List<Song>>
    fun observeRecentlyPlayed(limit: Int): Flow<List<Song>>
    fun observeFavorites(): Flow<List<Song>>
    suspend fun getSong(id: String): Song?
    suspend fun upsertSongs(songs: List<Song>)
    suspend fun setFavorite(songId: String, favorite: Boolean)
    suspend fun recordPlayback(song: Song, completed: Boolean, durationPlayedMs: Long)
}

interface LibraryRepository {
    fun observeAlbums(): Flow<List<Album>>
    fun observeArtists(): Flow<List<Artist>>
    fun observeGenres(): Flow<List<Genre>>
}

interface PlaylistRepository {
    fun observePlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(title: String, description: String): Playlist
    suspend fun renamePlaylist(playlistId: String, title: String)
    suspend fun deletePlaylist(playlistId: String)
    suspend fun addSong(playlistId: String, songId: String)
    suspend fun removeSong(playlistId: String, songId: String)
    suspend fun moveSong(playlistId: String, from: Int, to: Int)
}

interface SearchRepository {
    fun search(query: String, hiResOnly: Boolean = false, lyricsOnly: Boolean = false): Flow<SearchResult>
    fun observeRecentSearches(): Flow<List<String>>
    suspend fun recordSearch(query: String, resultCount: Int)
    suspend fun clearSearchHistory()
}

data class SearchResult(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val lyrics: List<Lyrics> = emptyList()
) { val totalCount: Int get() = songs.size + albums.size + artists.size + playlists.size + lyrics.size }

interface LyricsRepository {
    fun observeLyrics(songId: String): Flow<Lyrics?>
    suspend fun saveLyrics(lyrics: Lyrics)
}

interface SettingsRepository {
    fun observeSettings(): Flow<AuraSettings>
    suspend fun updateSettings(settings: AuraSettings)
}

interface PlaybackController {
    val playbackState: Flow<AuraPlaybackState>
    suspend fun play(song: Song, queue: List<Song> = listOf(song))
    suspend fun playQueue(queue: List<Song>, startIndex: Int)
    suspend fun togglePlayPause()
    suspend fun seekTo(positionMs: Long)
    suspend fun next()
    suspend fun previous()
    suspend fun setShuffle(enabled: Boolean)
    suspend fun setRepeatMode(mode: RepeatMode)
    suspend fun setSpeed(speed: Float)
}

interface ReplayRepository {
    fun observeReplaySummary(year: Int): Flow<ReplaySummary>
}
