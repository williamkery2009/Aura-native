package ai.arena.aura.feature.search

import ai.arena.aura.core.domain.model.Album
import ai.arena.aura.core.domain.model.Artist
import ai.arena.aura.core.domain.model.Song
import ai.arena.aura.core.domain.repository.SearchResult
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchResultTest {
    @Test fun totalCountIncludesAllResultGroups() {
        val result = SearchResult(
            songs = listOf(Song(id = "s", uri = "content://song", title = "Song")),
            albums = listOf(Album(id = "a", title = "Album", artist = "Artist")),
            artists = listOf(Artist(id = "r", name = "Artist"))
        )
        assertEquals(3, result.totalCount)
    }
}
