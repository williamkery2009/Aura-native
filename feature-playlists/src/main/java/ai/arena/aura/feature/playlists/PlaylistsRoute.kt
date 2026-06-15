package ai.arena.aura.feature.playlists

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
import ai.arena.aura.core.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel class PlaylistsViewModel @Inject constructor(private val repo: PlaylistRepository): ViewModel(){ val playlists=repo.observePlaylists().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()); fun create()=viewModelScope.launch{repo.createPlaylist("Untitled VIP Playlist", "Curated private offline audio execution list.")} }
@Composable fun PlaylistsRoute(viewModel: PlaylistsViewModel = hiltViewModel()){ val items by viewModel.playlists.collectAsStateWithLifecycle(); Column(Modifier.fillMaxSize()){ Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement=Arrangement.SpaceBetween){ Text("Custom Audio Playlists", style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Black); Button(onClick=viewModel::create){Text("+ CREATE")} }; LazyColumn{ items(items,key={it.id}){ ListItem(headlineContent={Text(it.title)}, supportingContent={Text("${it.songCount} songs • ${it.type}")}) } } } }
