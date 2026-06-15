package ai.arena.aura.core.media.playback

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import ai.arena.aura.core.domain.model.*
import ai.arena.aura.core.domain.repository.PlaybackController
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(UnstableApi::class)
@Singleton
class AuraPlaybackController @Inject constructor(@ApplicationContext context: Context) : PlaybackController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val player: ExoPlayer = ExoPlayer.Builder(context).setHandleAudioBecomingNoisy(true).build().apply {
        setWakeMode(C.WAKE_MODE_LOCAL)
    }
    private val songByMediaId = LinkedHashMap<String, Song>()
    private val mutableState = MutableStateFlow(AuraPlaybackState())
    override val playbackState: Flow<AuraPlaybackState> = mutableState.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) = publishState()
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = publishState()
            override fun onPlaybackStateChanged(playbackState: Int) = publishState()
            override fun onIsPlayingChanged(isPlaying: Boolean) = publishState()
        })
        scope.launch {
            while (isActive) {
                publishState()
                delay(500)
            }
        }
    }

    fun player(): ExoPlayer = player

    fun createSession(context: Context): MediaSession = MediaSession.Builder(context, player).setId("AuraPlaybackSession").build()

    override suspend fun play(song: Song, queue: List<Song>) = withContext(Dispatchers.Main) {
        val index = queue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        playQueue(queue.ifEmpty { listOf(song) }, index)
    }

    override suspend fun playQueue(queue: List<Song>, startIndex: Int) = withContext(Dispatchers.Main) {
        songByMediaId.clear()
        val items = queue.map { song ->
            songByMediaId[song.id] = song
            MediaItem.Builder().setMediaId(song.id).setUri(Uri.parse(song.uri)).setMediaMetadata(androidx.media3.common.MediaMetadata.Builder().setTitle(song.title).setArtist(song.artist).setAlbumTitle(song.album).setArtworkUri(song.artworkUri?.let(Uri::parse)).build()).build()
        }
        player.setMediaItems(items, startIndex.coerceIn(items.indices), 0L)
        player.prepare()
        player.play()
        publishState(queue)
    }

    override suspend fun togglePlayPause() = withContext(Dispatchers.Main) { if (player.isPlaying) player.pause() else player.play() }
    override suspend fun seekTo(positionMs: Long) = withContext(Dispatchers.Main) { player.seekTo(positionMs) }
    override suspend fun next() = withContext(Dispatchers.Main) { if (player.hasNextMediaItem()) player.seekToNextMediaItem() }
    override suspend fun previous() = withContext(Dispatchers.Main) { if (player.currentPosition > 3000) player.seekTo(0) else if (player.hasPreviousMediaItem()) player.seekToPreviousMediaItem() }
    override suspend fun setShuffle(enabled: Boolean) = withContext(Dispatchers.Main) { player.shuffleModeEnabled = enabled; publishState() }
    override suspend fun setRepeatMode(mode: RepeatMode) = withContext(Dispatchers.Main) { player.repeatMode = when(mode){ RepeatMode.Off -> Player.REPEAT_MODE_OFF; RepeatMode.All -> Player.REPEAT_MODE_ALL; RepeatMode.One -> Player.REPEAT_MODE_ONE }; publishState() }
    override suspend fun setSpeed(speed: Float) = withContext(Dispatchers.Main) { player.setPlaybackSpeed(speed.coerceIn(0.5f, 2.5f)); publishState() }

    private fun publishState(explicitQueue: List<Song>? = null) {
        val mediaId = player.currentMediaItem?.mediaId
        val current = mediaId?.let(songByMediaId::get)
        val repeat = when(player.repeatMode){ Player.REPEAT_MODE_ALL -> RepeatMode.All; Player.REPEAT_MODE_ONE -> RepeatMode.One; else -> RepeatMode.Off }
        mutableState.value = mutableState.value.copy(currentSong = current, isPlaying = player.isPlaying, positionMs = player.currentPosition.coerceAtLeast(0), durationMs = player.duration.takeIf { it > 0 } ?: current?.durationMs ?: 0, bufferedPositionMs = player.bufferedPosition, shuffleEnabled = player.shuffleModeEnabled, repeatMode = repeat, speed = player.playbackParameters.speed, queue = explicitQueue ?: mutableState.value.queue)
    }
}
