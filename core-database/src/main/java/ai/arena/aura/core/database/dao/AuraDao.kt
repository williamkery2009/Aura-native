package ai.arena.aura.core.database.dao

import androidx.room.*
import ai.arena.aura.core.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY sortTitle COLLATE NOCASE") fun observeSongs(): Flow<List<SongEntity>>
    @Query("SELECT * FROM songs WHERE id = :id") suspend fun getSong(id: String): SongEntity?
    @Query("SELECT * FROM songs ORDER BY dateAdded DESC LIMIT :limit") fun observeRecentlyAdded(limit: Int): Flow<List<SongEntity>>
    @Query("SELECT * FROM songs WHERE lastPlayedAt IS NOT NULL ORDER BY lastPlayedAt DESC LIMIT :limit") fun observeRecentlyPlayed(limit: Int): Flow<List<SongEntity>>
    @Query("SELECT * FROM songs WHERE isFavorite = 1 ORDER BY title COLLATE NOCASE") fun observeFavorites(): Flow<List<SongEntity>>
    @Upsert suspend fun upsert(song: SongEntity)
    @Upsert suspend fun upsertAll(songs: List<SongEntity>)
    @Query("UPDATE songs SET isFavorite = :favorite WHERE id = :songId") suspend fun setFavorite(songId: String, favorite: Boolean)
    @Query("UPDATE songs SET playCount = playCount + 1, lastPlayedAt = :time WHERE id = :songId") suspend fun incrementPlay(songId: String, time: Long)
    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%' OR genre LIKE '%' || :query || '%' ORDER BY playCount DESC, title COLLATE NOCASE LIMIT 100") fun searchSongs(query: String): Flow<List<SongEntity>>
    @Query("SELECT * FROM songs WHERE (sampleRate >= 88200 OR bitDepth >= 24 OR format IN ('DSD','DSF','DFF')) AND (title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%') ORDER BY sampleRate DESC, bitDepth DESC LIMIT 100") fun searchHiResSongs(query: String): Flow<List<SongEntity>>
}

@Dao
interface LibraryDao {
    @Query("SELECT COALESCE(albumId, album) AS id, album AS title, COALESCE(albumArtist, artist) AS artist, NULL AS releaseYear, genre, artworkUri, COUNT(*) AS songCount, SUM(durationMs) AS totalDurationMs, 0 AS isFavorite FROM songs GROUP BY COALESCE(albumId, album), album ORDER BY album COLLATE NOCASE") fun observeAlbums(): Flow<List<AlbumEntity>>
    @Query("SELECT COALESCE(artistId, artist) AS id, artist AS name, NULL AS biography, MAX(artworkUri) AS artworkUri, COUNT(*) AS songCount, COUNT(DISTINCT album) AS albumCount, 0 AS isFavorite FROM songs GROUP BY COALESCE(artistId, artist), artist ORDER BY artist COLLATE NOCASE") fun observeArtists(): Flow<List<ArtistEntity>>
    @Query("SELECT genre AS id, genre AS name, COUNT(*) AS songCount, COUNT(DISTINCT album) AS albumCount FROM songs GROUP BY genre ORDER BY genre COLLATE NOCASE") fun observeGenres(): Flow<List<GenreEntity>>
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY isPinned DESC, title COLLATE NOCASE") fun observePlaylists(): Flow<List<PlaylistEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(playlist: PlaylistEntity)
    @Query("UPDATE playlists SET title = :title, dateModified = :updatedAt WHERE id = :playlistId") suspend fun rename(playlistId: String, title: String, updatedAt: Long)
    @Query("DELETE FROM playlists WHERE id = :playlistId") suspend fun delete(playlistId: String)
    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM playlist_songs WHERE playlistId = :playlistId") suspend fun nextPosition(playlistId: String): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSong(song: PlaylistSongEntity)
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId") suspend fun removeSong(playlistId: String, songId: String)
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position") suspend fun songsForPlaylist(playlistId: String): List<PlaylistSongEntity>
    @Upsert suspend fun upsertPlaylistSongs(songs: List<PlaylistSongEntity>)
}

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(history: HistoryEntity)
    @Query("SELECT * FROM history WHERE playedAt BETWEEN :from AND :to ORDER BY playedAt DESC") fun observeHistory(from: Long, to: Long): Flow<List<HistoryEntity>>
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE key = :key") fun observeSetting(key: String): Flow<SettingEntity?>
    @Query("SELECT * FROM settings WHERE key = :key") suspend fun getSetting(key: String): SettingEntity?
    @Upsert suspend fun upsert(setting: SettingEntity)
}

@Dao
interface LyricsDao {
    @Query("SELECT * FROM lyrics WHERE songId = :songId LIMIT 1") fun observeLyrics(songId: String): Flow<LyricsEntity?>
    @Query("SELECT * FROM lyrics WHERE rawText LIKE '%' || :query || '%' LIMIT 50") fun searchLyrics(query: String): Flow<List<LyricsEntity>>
    @Upsert suspend fun upsert(lyrics: LyricsEntity)
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT value FROM settings WHERE key LIKE 'search:%' ORDER BY updatedAt DESC LIMIT 20") fun observeRecentSearches(): Flow<List<String>>
    @Query("DELETE FROM settings WHERE key LIKE 'search:%'") suspend fun clear()
}
