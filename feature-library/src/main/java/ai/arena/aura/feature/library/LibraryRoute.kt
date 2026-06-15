package ai.arena.aura.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

enum class LibraryTab { Songs, Albums, Artists, Genres, Favorites }
data class LibraryUiState(val tab: LibraryTab = LibraryTab.Songs, val songs: List<Song> = emptyList(), val albums: List<Album> = emptyList(), val artists: List<Artist> = emptyList(), val genres: List<Genre> = emptyList(), val favorites: List<Song> = emptyList())
@HiltViewModel class LibraryViewModel @Inject constructor(private val songsRepo: SongRepository, private val library: LibraryRepository, private val playback: PlaybackController) : ViewModel() {
    private val tab = MutableStateFlow(LibraryTab.Songs)
    val state = combine(tab, songsRepo.observeSongs(), library.observeAlbums(), library.observeArtists(), library.observeGenres(), songsRepo.observeFavorites()) { t, s, a, ar, g, f -> LibraryUiState(t, s, a, ar, g, f) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LibraryUiState())
    fun setTab(t: LibraryTab) { tab.value = t }
    fun play(song: Song, queue: List<Song>) = viewModelScope.launch { playback.play(song, queue) }
}
@Composable fun LibraryRoute(onRequestScan: () -> Unit, viewModel: LibraryViewModel = hiltViewModel()) { val state by viewModel.state.collectAsStateWithLifecycle(); Column(Modifier.fillMaxSize()) { Text("Master Library", Modifier.padding(22.dp), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black); ScrollableTabRow(selectedTabIndex = state.tab.ordinal) { LibraryTab.entries.forEach { Tab(selected = state.tab == it, onClick = { viewModel.setTab(it) }, text = { Text(it.name) }) } }; if (state.songs.isEmpty()) EmptyAuraState("No Physical Exhibits Scanned", "Scan your Android audio library to unlock Albums, Artists, Genres, Downloads, Favorites and Hi-Res views.", "SCAN DEVICE AUDIO", onRequestScan) else LazyColumn(contentPadding = PaddingValues(bottom = 110.dp)) { when(state.tab){ LibraryTab.Songs -> items(state.songs, key={it.id}) { SongRow(it, onClick = { viewModel.play(it, state.songs) }) }; LibraryTab.Favorites -> items(state.favorites, key={it.id}) { SongRow(it, onClick = { viewModel.play(it, state.favorites) }) }; LibraryTab.Albums -> items(state.albums, key={it.id}) { ListItem(headlineContent={Text(it.title)}, supportingContent={Text("${it.artist} • ${it.songCount} songs")}) }; LibraryTab.Artists -> items(state.artists, key={it.id}) { ListItem(headlineContent={Text(it.name)}, supportingContent={Text("${it.songCount} songs • ${it.albumCount} albums")}) }; LibraryTab.Genres -> items(state.genres, key={it.id}) { ListItem(headlineContent={Text(it.name)}, supportingContent={Text("${it.songCount} songs")}) } } } } }
