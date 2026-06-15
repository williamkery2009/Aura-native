package ai.arena.aura.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.arena.aura.core.domain.model.*
import ai.arena.aura.core.domain.repository.*
import ai.arena.aura.core.ui.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(val recentlyAdded: List<Song> = emptyList(), val recentlyPlayed: List<Song> = emptyList(), val favorites: List<Song> = emptyList(), val artists: List<Artist> = emptyList(), val playlists: List<Playlist> = emptyList())

@HiltViewModel
class HomeViewModel @Inject constructor(private val songs: SongRepository, private val library: LibraryRepository, private val playlistsRepository: PlaylistRepository, private val playback: PlaybackController) : ViewModel() {
    val state: StateFlow<HomeUiState> = combine(songs.observeRecentlyAdded(20), songs.observeRecentlyPlayed(20), songs.observeFavorites(), library.observeArtists(), playlistsRepository.observePlaylists()) { added, played, fav, artists, playlists -> HomeUiState(added, played, fav, artists, playlists) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
    fun play(song: Song, queue: List<Song>) = viewModelScope.launch { playback.play(song, queue) }
}

@Composable
fun HomeRoute(onOpenReplay: () -> Unit, onRequestScan: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.recentlyAdded.isEmpty() && state.recentlyPlayed.isEmpty()) EmptyAuraState("Your Private Media Vault is Empty.", "Scan your device to build an uncompromising 100% offline Aura library.", "SCAN DEVICE AUDIO", onRequestScan) else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 110.dp)) {
        item { Text("Listen Now.", Modifier.padding(22.dp, 26.dp, 22.dp, 6.dp), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black) }
        item { Button(onClick = onOpenReplay, Modifier.padding(horizontal = 20.dp).fillMaxWidth()) { Text("Explore Breathtaking Replay Report") } }
        shelf("Recently Played", state.recentlyPlayed.ifEmpty { state.recentlyAdded }, viewModel::play)
        shelf("Recently Added", state.recentlyAdded, viewModel::play)
        shelf("VIP Archive", state.favorites, viewModel::play)
    }
}
private fun androidx.compose.foundation.lazy.LazyListScope.shelf(title: String, songs: List<Song>, play: (Song, List<Song>) -> Unit) { if (songs.isNotEmpty()) { item { SectionHeader(title) }; item { LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) { items(songs, key = { it.id }) { song -> LiquidGlassCard(Modifier.width(190.dp)) { AlbumArt(song.artworkUri, song.title, Modifier.fillMaxWidth().height(170.dp)); Text(song.title, Modifier.padding(12.dp, 10.dp, 12.dp, 0.dp), maxLines = 1, fontWeight = FontWeight.Bold); Text(song.artist, Modifier.padding(12.dp, 0.dp, 12.dp, 12.dp), maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant); Button(onClick = { play(song, songs) }, Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth()) { Text("Play") } } } } } } }
