package ai.arena.aura.core.database.mapper

import ai.arena.aura.core.database.entity.*
import ai.arena.aura.core.domain.model.*

fun SongEntity.toDomain() = Song(id, uri, title, sortTitle, artistId, artist, albumId, album, albumArtist, genre, composer, trackNumber, discNumber, durationMs, format, mimeType, sampleRate, bitDepth, bitrateKbps, channelCount, fileSizeBytes, dateAdded, dateModified, lastPlayedAt, playCount, rating, isFavorite, isDownloaded, folderPath, artworkUri, dominantColor, accentColor, replayGainTrack, replayGainAlbum, lyricsId)
fun Song.toEntity() = SongEntity(id, uri, title, sortTitle, artistId, artist, albumId, album, albumArtist, genre, composer, trackNumber, discNumber, durationMs, format, mimeType, sampleRate, bitDepth, bitrateKbps, channelCount, fileSizeBytes, dateAdded, dateModified, lastPlayedAt, playCount, rating, isFavorite, isDownloaded, folderPath, artworkUri, dominantColor, accentColor, replayGainTrack, replayGainAlbum, lyricsId)
fun AlbumEntity.toDomain() = Album(id, title, artist, releaseYear, genre, artworkUri, songCount, totalDurationMs, isFavorite)
fun ArtistEntity.toDomain() = Artist(id, name, biography, artworkUri, songCount, albumCount, isFavorite)
fun GenreEntity.toDomain() = Genre(id, name, songCount, albumCount)
fun PlaylistEntity.toDomain() = Playlist(id, title, description, runCatching { PlaylistType.valueOf(type) }.getOrDefault(PlaylistType.Manual), artworkUri, dateCreated, dateModified, isPinned, isSmart, songCount, totalDurationMs)
fun LyricsEntity.toDomain(lines: List<LyricsLine> = emptyList()) = Lyrics(id, songId, runCatching { LyricsType.valueOf(type) }.getOrDefault(LyricsType.Unsynced), language, rawText, lines)
