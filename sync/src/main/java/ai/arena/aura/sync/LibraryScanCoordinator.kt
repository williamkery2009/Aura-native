package ai.arena.aura.sync

import ai.arena.aura.core.domain.repository.SongRepository
import ai.arena.aura.core.media.scanner.MediaStoreScanner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryScanCoordinator @Inject constructor(private val scanner: MediaStoreScanner, private val songs: SongRepository) {
    suspend fun scanDeviceAudio(): Int {
        val scanned = scanner.scanAudio()
        if (scanned.isNotEmpty()) songs.upsertSongs(scanned)
        return scanned.size
    }
}
