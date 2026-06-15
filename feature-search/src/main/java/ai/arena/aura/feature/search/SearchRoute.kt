package ai.arena.aura.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.arena.aura.core.domain.model.Song
import ai.arena.aura.core.domain.repository.*
import ai.arena.aura.core.ui.SongRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel class SearchViewModel @Inject constructor(private val search: SearchRepository, private val playback: PlaybackController) : ViewModel() { private val query = MutableStateFlow(""); val state = query.debounce(180).flatMapLatest { q -> search.search(q) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResult()); val q = query.asStateFlow(); fun setQuery(v: String){ query.value = v }; fun play(song: Song, queue: List<Song>) = viewModelScope.launch { playback.play(song, queue); search.recordSearch(query.value, state.value.totalCount) } }
@Composable fun SearchRoute(viewModel: SearchViewModel = hiltViewModel()) { val result by viewModel.state.collectAsStateWithLifecycle(); val q by viewModel.q.collectAsStateWithLifecycle(); Column(Modifier.fillMaxSize()) { Text("Search", Modifier.padding(22.dp), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black); OutlinedTextField(value=q, onValueChange=viewModel::setQuery, placeholder={Text("Search private catalog (Songs, Artists, Albums, Synced Lyrics)...")}, leadingIcon={Icon(Icons.Rounded.Search,null)}, modifier=Modifier.padding(horizontal=16.dp).fillMaxWidth(), singleLine=true); LazyColumn(contentPadding=PaddingValues(bottom=110.dp, top=12.dp)) { if(q.isNotBlank() && result.totalCount==0) item { ListItem(headlineContent={Text("No Direct Matches Found")}, supportingContent={Text("Clear or refine your search terms.")}) }; items(result.songs, key={it.id}) { SongRow(it, onClick={viewModel.play(it,result.songs)}) }; items(result.albums, key={"a-${it.id}"}) { ListItem(headlineContent={Text(it.title)}, supportingContent={Text("Album • ${it.artist}")}) }; items(result.artists, key={"r-${it.id}"}) { ListItem(headlineContent={Text(it.name)}, supportingContent={Text("Artist • ${it.songCount} songs")}) }; items(result.lyrics, key={"l-${it.id}"}) { ListItem(headlineContent={Text("Lyrics match")}, supportingContent={Text(it.rawText.take(120))}) } } } }
