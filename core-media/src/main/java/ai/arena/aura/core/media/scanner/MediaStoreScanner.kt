package ai.arena.aura.core.media.scanner

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import ai.arena.aura.core.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreScanner @Inject constructor(@ApplicationContext private val context: Context) {
    suspend fun scanAudio(): List<Song> = withContext(Dispatchers.IO) {
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DATE_MODIFIED, MediaStore.Audio.Media.TRACK, MediaStore.Audio.Media.RELATIVE_PATH)
        val songs = mutableListOf<Song>()
        context.contentResolver.query(collection, projection, "${MediaStore.Audio.Media.IS_MUSIC}=1", null, "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC")?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val modifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val pathCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
            while (cursor.moveToNext()) {
                val mediaId = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(collection, mediaId).toString()
                val title = cursor.getString(titleCol).orEmpty().ifBlank { "Unknown Track" }
                val artist = cursor.getString(artistCol).orEmpty().ifBlank { "Unknown Performer" }
                val album = cursor.getString(albumCol).orEmpty().ifBlank { "Private Offline Collection" }
                val mime = cursor.getString(mimeCol)
                val format = mime?.substringAfterLast('/')?.uppercase(Locale.US) ?: "UNKNOWN"
                val art = ContentUris.withAppendedId(android.net.Uri.parse("content://media/external/audio/albumart"), mediaId).toString()
                songs += Song(id = "mediastore-$mediaId", uri = uri, title = title, artist = artist, artistId = artist.stableId("artist"), album = album, albumId = album.stableId("album"), durationMs = cursor.getLong(durationCol), mimeType = mime, format = format, fileSizeBytes = cursor.getLong(sizeCol), dateAdded = cursor.getLong(addedCol) * 1000L, dateModified = cursor.getLong(modifiedCol) * 1000L, trackNumber = cursor.getInt(trackCol) % 1000, folderPath = cursor.getString(pathCol), artworkUri = art)
            }
        }
        songs
    }
    private fun String.stableId(prefix: String) = "$prefix-${lowercase(Locale.US).trim().hashCode()}"
}
