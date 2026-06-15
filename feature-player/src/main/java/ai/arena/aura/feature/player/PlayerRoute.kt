package ai.arena.aura.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.arena.aura.core.common.AuraFormatters
import ai.arena.aura.core.domain.model.*
import ai.arena.aura.core.domain.repository.PlaybackController
import ai.arena.aura.core.ui.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class PlayerViewModel @Inject constructor(private val playback: PlaybackController) : ViewModel() { val state = playback.playbackState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuraPlaybackState()); fun toggle()=viewModelScope.launch{playback.togglePlayPause()}; fun next()=viewModelScope.launch{playback.next()}; fun prev()=viewModelScope.launch{playback.previous()}; fun seek(v:Float)=viewModelScope.launch{playback.seekTo(v.toLong())}; fun shuffle(v:Boolean)=viewModelScope.launch{playback.setShuffle(v)}; fun repeat(mode:RepeatMode)=viewModelScope.launch{playback.setRepeatMode(mode)} }
@Composable fun FullPlayerRoute(onClose: () -> Unit, viewModel: PlayerViewModel = hiltViewModel()) { val state by viewModel.state.collectAsStateWithLifecycle(); val song = state.currentSong; if(song==null){ EmptyAuraState("Nothing Playing", "Choose a song from your Aura library to open the Now Playing Sanctuary."); return }; Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment=Alignment.CenterHorizontally) { Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically){ IconButton(onClick=onClose){Icon(Icons.Rounded.KeyboardArrowDown,null)}; Text("Now Playing Sanctuary", fontWeight=FontWeight.Black); IconButton(onClick={}){Icon(Icons.Rounded.Tune,null)} }; AlbumArt(song.artworkUri, song.title, Modifier.fillMaxWidth().weight(1f).padding(20.dp)); Text(song.title, style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Black); Text(song.artist, color=MaterialTheme.colorScheme.onSurfaceVariant); SpectrumBars(Modifier.fillMaxWidth().height(90.dp).padding(vertical=18.dp), state.isPlaying); Slider(value=state.positionMs.toFloat().coerceIn(0f, state.durationMs.toFloat().coerceAtLeast(1f)), onValueChange=viewModel::seek, valueRange=0f..state.durationMs.toFloat().coerceAtLeast(1f)); Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text(AuraFormatters.duration(state.positionMs)); Text(AuraFormatters.duration(state.durationMs)) }; Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(18.dp)){ IconButton(onClick={viewModel.shuffle(!state.shuffleEnabled)}){Icon(Icons.Rounded.Shuffle,null, tint=if(state.shuffleEnabled) MaterialTheme.colorScheme.primary else LocalContentColor.current)}; IconButton(onClick=viewModel::prev){Icon(Icons.Rounded.SkipPrevious,null, Modifier.size(40.dp))}; FilledIconButton(onClick=viewModel::toggle, modifier=Modifier.size(72.dp)){Icon(if(state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,null, Modifier.size(42.dp))}; IconButton(onClick=viewModel::next){Icon(Icons.Rounded.SkipNext,null, Modifier.size(40.dp))}; IconButton(onClick={viewModel.repeat(if(state.repeatMode==RepeatMode.Off) RepeatMode.All else if(state.repeatMode==RepeatMode.All) RepeatMode.One else RepeatMode.Off)}){Icon(Icons.Rounded.Repeat,null, tint=if(state.repeatMode!=RepeatMode.Off) MaterialTheme.colorScheme.primary else LocalContentColor.current)} } } }
@Composable fun PlayerMiniHost(onExpand: () -> Unit, viewModel: PlayerViewModel = hiltViewModel()) { val state by viewModel.state.collectAsStateWithLifecycle(); val song=state.currentSong ?: return; MiniPlayer(song,state.isPlaying,viewModel::toggle,onExpand,viewModel::next) }
