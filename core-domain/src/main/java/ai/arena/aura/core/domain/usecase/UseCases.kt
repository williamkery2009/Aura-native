package ai.arena.aura.core.domain.usecase

import ai.arena.aura.core.domain.model.Song
import ai.arena.aura.core.domain.repository.*
import javax.inject.Inject

class ObserveHomeUseCase @Inject constructor(
    private val songs: SongRepository,
    private val library: LibraryRepository,
    private val playlists: PlaylistRepository
) {
    fun recentlyAdded(limit: Int = 24) = songs.observeRecentlyAdded(limit)
    fun recentlyPlayed(limit: Int = 24) = songs.observeRecentlyPlayed(limit)
    fun favorites() = songs.observeFavorites()
    fun artists() = library.observeArtists()
    fun playlists() = playlists.observePlaylists()
}

class PlaySongUseCase @Inject constructor(private val playback: PlaybackController) {
    suspend operator fun invoke(song: Song, queue: List<Song>) = playback.play(song, queue)
}

class ToggleFavoriteUseCase @Inject constructor(private val songs: SongRepository) {
    suspend operator fun invoke(song: Song) = songs.setFavorite(song.id, !song.isFavorite)
}

class SearchCatalogUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    operator fun invoke(query: String, hiResOnly: Boolean = false, lyricsOnly: Boolean = false) = searchRepository.search(query, hiResOnly, lyricsOnly)
}
