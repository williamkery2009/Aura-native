package ai.arena.aura.feature.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ai.arena.aura.core.domain.repository.SongRepository
import ai.arena.aura.core.ui.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
@HiltViewModel class DownloadsViewModel @Inject constructor(repo: SongRepository): ViewModel(){ val downloads=repo.observeSongs().map{list->list.filter{it.isDownloaded}}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()) }
@Composable fun DownloadsRoute(viewModel: DownloadsViewModel = hiltViewModel()){ val songs by viewModel.downloads.collectAsStateWithLifecycle(); if(songs.isEmpty()) EmptyAuraState("Downloads", "No offline network downloads are queued. Local device songs remain available through Library.") else LazyColumn(Modifier.fillMaxSize()){ items(songs,key={it.id}){ SongRow(it,onClick={}) } } }
