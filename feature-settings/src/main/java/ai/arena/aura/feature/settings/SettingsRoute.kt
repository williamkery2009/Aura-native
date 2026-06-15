package ai.arena.aura.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.arena.aura.core.domain.model.*
import ai.arena.aura.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel class SettingsViewModel @Inject constructor(private val repo: SettingsRepository): ViewModel(){ val settings=repo.observeSettings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuraSettings()); fun update(s:AuraSettings)=viewModelScope.launch{repo.updateSettings(s)} }
@Composable fun SettingsRoute(viewModel: SettingsViewModel = hiltViewModel()){ val s by viewModel.settings.collectAsStateWithLifecycle(); LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(20.dp)){ item{ Text("Aura Master Settings", style=MaterialTheme.typography.headlineLarge, fontWeight=FontWeight.Black); Spacer(Modifier.height(16.dp)) }; item{ SettingsSwitch("Sample-Accurate Gapless Playback", s.gaplessEnabled){viewModel.update(s.copy(gaplessEnabled=it))} }; item{ SettingsSwitch("High Contrast Structural Border Override", s.highContrast){viewModel.update(s.copy(highContrast=it))} }; item{ SettingsSwitch("Reduced Motion Kinematic Override", s.reducedMotion){viewModel.update(s.copy(reducedMotion=it))} }; item{ Text("Organic Crossfade Stitching Overlap: ${s.crossfadeSeconds}s", Modifier.padding(top=18.dp)); Slider(value=s.crossfadeSeconds.toFloat(), onValueChange={viewModel.update(s.copy(crossfadeSeconds=it.toInt()))}, valueRange=0f..12f, steps=11) }; item{ Text("ReplayGain Volume Leveling Policy", fontWeight=FontWeight.Bold, modifier=Modifier.padding(top=18.dp)); ReplayGainMode.entries.forEach{ mode -> FilterChip(selected=s.replayGainMode==mode, onClick={viewModel.update(s.copy(replayGainMode=mode))}, label={Text(mode.name)}, modifier=Modifier.padding(end=8.dp)) } } } }
@Composable private fun SettingsSwitch(text:String, checked:Boolean, onChecked:(Boolean)->Unit){ ListItem(headlineContent={Text(text)}, trailingContent={Switch(checked,onChecked)}) }
