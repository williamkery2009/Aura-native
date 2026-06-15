package ai.arena.aura.core.database.di

import android.content.Context
import androidx.room.Room
import ai.arena.aura.core.database.AuraDatabase
import ai.arena.aura.core.database.repository.*
import ai.arena.aura.core.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseProviderModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AuraDatabase = Room.databaseBuilder(context, AuraDatabase::class.java, "aura_music.db").enableMultiInstanceInvalidation().build()
    @Provides fun songDao(db: AuraDatabase) = db.songDao()
    @Provides fun libraryDao(db: AuraDatabase) = db.libraryDao()
    @Provides fun playlistDao(db: AuraDatabase) = db.playlistDao()
    @Provides fun historyDao(db: AuraDatabase) = db.historyDao()
    @Provides fun settingsDao(db: AuraDatabase) = db.settingsDao()
    @Provides fun lyricsDao(db: AuraDatabase) = db.lyricsDao()
    @Provides fun searchHistoryDao(db: AuraDatabase) = db.searchHistoryDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {
    @Binds abstract fun bindSongRepository(repository: RoomSongRepository): SongRepository
    @Binds abstract fun bindLibraryRepository(repository: RoomLibraryRepository): LibraryRepository
    @Binds abstract fun bindPlaylistRepository(repository: RoomPlaylistRepository): PlaylistRepository
    @Binds abstract fun bindSearchRepository(repository: RoomSearchRepository): SearchRepository
    @Binds abstract fun bindLyricsRepository(repository: RoomLyricsRepository): LyricsRepository
    @Binds abstract fun bindSettingsRepository(repository: RoomSettingsRepository): SettingsRepository
    @Binds abstract fun bindReplayRepository(repository: RoomReplayRepository): ReplayRepository
}
