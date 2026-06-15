package ai.arena.aura.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ai.arena.aura.core.database.dao.*
import ai.arena.aura.core.database.entity.*

@Database(
    entities = [SongEntity::class, AlbumEntity::class, ArtistEntity::class, GenreEntity::class, PlaylistEntity::class, PlaylistSongEntity::class, FavoriteEntity::class, DownloadEntity::class, HistoryEntity::class, QueueEntity::class, SettingEntity::class, LyricsEntity::class, FolderEntity::class, SongFtsEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun libraryDao(): LibraryDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun historyDao(): HistoryDao
    abstract fun settingsDao(): SettingsDao
    abstract fun lyricsDao(): LyricsDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
