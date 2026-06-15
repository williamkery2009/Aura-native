package ai.arena.aura.core.database

import ai.arena.aura.core.database.mapper.toEntity
import ai.arena.aura.core.database.mapper.toDomain
import ai.arena.aura.core.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {
    @Test fun songRoundTripPreservesIdentity() {
        val song = Song(id = "1", uri = "content://song/1", title = "Track", artist = "Artist", album = "Album", durationMs = 1000)
        assertEquals(song.id, song.toEntity().toDomain().id)
        assertEquals(song.title, song.toEntity().toDomain().title)
    }
}
