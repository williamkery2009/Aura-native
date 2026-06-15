package ai.arena.aura.core.database.repository

import ai.arena.aura.core.database.dao.*
import ai.arena.aura.core.database.entity.*
import ai.arena.aura.core.database.mapper.*
import ai.arena.aura.core.domain.model.*
import ai.arena.aura.core.domain.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomSongRepository @Inject constructor(private val songDao: SongDao, private val historyDao: HistoryDao) : SongRepository {
    override fun observeSongs(): Flow<List<Song>> = songDao.observeSongs().map { list -> list.map { it.toDomain() } }
    override fun observeRecentlyAdded(limit: Int): Flow<List<Song>> = songDao.observeRecentlyAdded(limit).map { it.map { entity -> entity.toDomain() } }
    override fun observeRecentlyPlayed(limit: Int): Flow<List<Song>> = songDao.observeRecentlyPlayed(limit).map { it.map { entity -> entity.toDomain() } }
    override fun observeFavorites(): Flow<List<Song>> = songDao.observeFavorites().map { it.map { entity -> entity.toDomain() } }
    override suspend fun getSong(id: String): Song? = songDao.getSong(id)?.toDomain()
    override suspend fun upsertSongs(songs: List<Song>) = songDao.upsertAll(songs.map { it.toEntity() })
    override suspend fun setFavorite(songId: String, favorite: Boolean) = songDao.setFavorite(songId, favorite)
    override suspend fun recordPlayback(song: Song, completed: Boolean, durationPlayedMs: Long) {
        val now = System.currentTimeMillis()
        songDao.incrementPlay(song.id, now)
        historyDao.insert(HistoryEntity("history-${UUID.randomUUID()}", song.id, song.title, song.artist, song.album, song.genre, song.format, now, durationPlayedMs, completed, song.artworkUri))
    }
}

@Singleton
class RoomLibraryRepository @Inject constructor(private val libraryDao: LibraryDao) : LibraryRepository {
    override fun observeAlbums(): Flow<List<Album>> = libraryDao.observeAlbums().map { it.map { entity -> entity.toDomain() } }
    override fun observeArtists(): Flow<List<Artist>> = libraryDao.observeArtists().map { it.map { entity -> entity.toDomain() } }
    override fun observeGenres(): Flow<List<Genre>> = libraryDao.observeGenres().map { it.map { entity -> entity.toDomain() } }
}

@Singleton
class RoomPlaylistRepository @Inject constructor(private val playlistDao: PlaylistDao) : PlaylistRepository {
    override fun observePlaylists(): Flow<List<Playlist>> = playlistDao.observePlaylists().map { it.map { entity -> entity.toDomain() } }
    override suspend fun createPlaylist(title: String, description: String): Playlist {
        val now = System.currentTimeMillis()
        val playlist = PlaylistEntity("playlist-${UUID.randomUUID()}", title.trim().ifBlank { "Untitled VIP Playlist" }, description.trim().ifBlank { "Curated private offline audio execution list." }, PlaylistType.Manual.name, null, now, now, false, false, 0, 0)
        playlistDao.insert(playlist)
        return playlist.toDomain()
    }
    override suspend fun renamePlaylist(playlistId: String, title: String) = playlistDao.rename(playlistId, title.trim().ifBlank { "Untitled VIP Playlist" }, System.currentTimeMillis())
    override suspend fun deletePlaylist(playlistId: String) = playlistDao.delete(playlistId)
    override suspend fun addSong(playlistId: String, songId: String) = playlistDao.insertSong(PlaylistSongEntity(playlistId, songId, playlistDao.nextPosition(playlistId), System.currentTimeMillis()))
    override suspend fun removeSong(playlistId: String, songId: String) = playlistDao.removeSong(playlistId, songId)
    override suspend fun moveSong(playlistId: String, from: Int, to: Int) {
        val list = playlistDao.songsForPlaylist(playlistId).toMutableList()
        if (from !in list.indices || to !in list.indices) return
        val item = list.removeAt(from)
        list.add(to, item)
        playlistDao.upsertPlaylistSongs(list.mapIndexed { index, entity -> entity.copy(position = index) })
    }
}

@Singleton
class RoomSearchRepository @Inject constructor(
    private val songDao: SongDao,
    private val lyricsDao: LyricsDao,
    private val settingsDao: SettingsDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val libraryDao: LibraryDao,
    private val playlistDao: PlaylistDao
) : SearchRepository {
    override fun search(query: String, hiResOnly: Boolean, lyricsOnly: Boolean): Flow<SearchResult> {
        val normalized = query.trim()
        if (normalized.isEmpty()) return flowOf(SearchResult())
        val songFlow = if (lyricsOnly) flowOf(emptyList()) else if (hiResOnly) songDao.searchHiResSongs(normalized) else songDao.searchSongs(normalized)
        return combine(songFlow, libraryDao.observeAlbums(), libraryDao.observeArtists(), playlistDao.observePlaylists(), lyricsDao.searchLyrics(normalized)) { songs, albums, artists, playlists, lyrics ->
            val q = normalized.lowercase()
            SearchResult(
                songs = songs.map { it.toDomain() },
                albums = if (lyricsOnly) emptyList() else albums.filter { it.title.lowercase().contains(q) || it.artist.lowercase().contains(q) }.take(30).map { it.toDomain() },
                artists = if (lyricsOnly) emptyList() else artists.filter { it.name.lowercase().contains(q) }.take(30).map { it.toDomain() },
                playlists = if (lyricsOnly) emptyList() else playlists.filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }.take(30).map { it.toDomain() },
                lyrics = lyrics.map { it.toDomain() }
            )
        }
    }
    override fun observeRecentSearches(): Flow<List<String>> = searchHistoryDao.observeRecentSearches()
    override suspend fun recordSearch(query: String, resultCount: Int) {
        val clean = query.trim()
        if (clean.isNotEmpty()) settingsDao.upsert(SettingEntity("search:${clean.lowercase()}", clean, System.currentTimeMillis()))
    }
    override suspend fun clearSearchHistory() = searchHistoryDao.clear()
}

@Singleton
class RoomLyricsRepository @Inject constructor(private val lyricsDao: LyricsDao) : LyricsRepository {
    override fun observeLyrics(songId: String): Flow<Lyrics?> = lyricsDao.observeLyrics(songId).map { it?.toDomain() }
    override suspend fun saveLyrics(lyrics: Lyrics) = lyricsDao.upsert(LyricsEntity(lyrics.id, lyrics.songId, lyrics.type.name, lyrics.language, "user", lyrics.rawText, Json.encodeToString(lyrics.lines), System.currentTimeMillis(), System.currentTimeMillis()))
}

@Singleton
class RoomSettingsRepository @Inject constructor(private val settingsDao: SettingsDao) : SettingsRepository {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    override fun observeSettings(): Flow<AuraSettings> = settingsDao.observeSetting("aura_settings").map { entity -> entity?.let { json.decodeFromString<AuraSettings>(it.value) } ?: AuraSettings() }
    override suspend fun updateSettings(settings: AuraSettings) = settingsDao.upsert(SettingEntity("aura_settings", json.encodeToString(settings), System.currentTimeMillis()))
}

@Singleton
class RoomReplayRepository @Inject constructor(private val historyDao: HistoryDao) : ReplayRepository {
    override fun observeReplaySummary(year: Int): Flow<ReplaySummary> {
        val start = java.time.LocalDate.of(year, 1, 1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = java.time.LocalDate.of(year + 1, 1, 1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        return historyDao.observeHistory(start, end).map { events ->
            val total = events.sumOf { it.durationPlayedMs }
            ReplaySummary(totalListeningMs = total, totalPlays = events.size, hiResPlayCount = events.count { it.format in setOf("FLAC", "ALAC", "DSD", "DSF", "DFF", "WAV", "AIFF") }, topGenres = events.groupBy { it.genre }.mapValues { it.value.sumOf { h -> h.durationPlayedMs } }.toList().sortedByDescending { it.second }.take(5), topArtists = events.groupingBy { it.artist }.eachCount().toList().sortedByDescending { it.second }.take(5), topSongs = events.groupingBy { it.title }.eachCount().toList().sortedByDescending { it.second }.take(5), longestStreakDays = computeStreak(events.map { it.playedAt }))
        }
    }
    private fun computeStreak(times: List<Long>): Int {
        val days = times.map { java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate() }.distinct().sorted()
        var best = 0; var current = 0; var prev: java.time.LocalDate? = null
        for (day in days) { current = if (prev == null || prev!!.plusDays(1) == day) current + 1 else 1; best = maxOf(best, current); prev = day }
        return best
    }
}
