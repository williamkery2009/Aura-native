package ai.arena.aura.feature.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
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
import ai.arena.aura.core.domain.model.Lyrics
import ai.arena.aura.core.domain.repository.LyricsRepository
import ai.arena.aura.core.ui.EmptyAuraState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel class LyricsViewModel @Inject constructor(repo: LyricsRepository, savedStateHandle: SavedStateHandle) : ViewModel() { private val songId = savedStateHandle.get<String>("songId").orEmpty(); val lyrics = repo.observeLyrics(songId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null) }
@Composable fun LyricsRoute(viewModel: LyricsViewModel = hiltViewModel()) { val lyrics by viewModel.lyrics.collectAsStateWithLifecycle(); if(lyrics==null) EmptyAuraState("Lyrics Not Embedded", "Synchronized Enhanced Lyrics manifest is not available for this local rip.") else LazyColumn(Modifier.fillMaxSize().padding(20.dp)) { item { Text("Karaoke Sync", style=MaterialTheme.typography.headlineLarge, fontWeight=FontWeight.Black) }; items(lyrics!!.lines) { Text(it.text, Modifier.padding(vertical=12.dp), style=MaterialTheme.typography.titleLarge); it.translation?.let { tr -> Text(tr, color=MaterialTheme.colorScheme.onSurfaceVariant) } } } }
