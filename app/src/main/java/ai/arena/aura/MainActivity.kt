package ai.arena.aura

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import ai.arena.aura.core.design.AuraTheme
import ai.arena.aura.feature.home.HomeRoute
import ai.arena.aura.feature.library.LibraryRoute
import ai.arena.aura.feature.search.SearchRoute
import ai.arena.aura.feature.player.FullPlayerRoute
import ai.arena.aura.feature.player.PlayerMiniHost
import ai.arena.aura.feature.playlists.PlaylistsRoute
import ai.arena.aura.feature.downloads.DownloadsRoute
import ai.arena.aura.feature.settings.SettingsRoute
import ai.arena.aura.analytics.AuraAnalytics
import ai.arena.aura.sync.LibraryScanCoordinator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var scanCoordinator: LibraryScanCoordinator
    private var scanMessage by mutableStateOf<String?>(null)
    private val audioPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) scanLibrary() else scanMessage = "Audio permission is required to scan your local Aura library." }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AuraApp(scanMessage = scanMessage, onMessageShown = { scanMessage = null }, onRequestScan = ::requestScan) }
    }
    private fun requestScan() {
        val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
        audioPermission.launch(permission)
    }
    private fun scanLibrary() { lifecycleScope.launch { val count = scanCoordinator.scanDeviceAudio(); scanMessage = "Aura imported $count local audio files into your private vault." } }
}

private enum class AuraRoute(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) { Home("home", "Listen Now", Icons.Rounded.Home), Library("library", "Library", Icons.Rounded.LibraryMusic), Search("search", "Search", Icons.Rounded.Search), Playlists("playlists", "Playlists", Icons.Rounded.QueueMusic), Settings("settings", "Settings", Icons.Rounded.Settings) }

@Composable
private fun AuraApp(scanMessage: String?, onMessageShown: () -> Unit, onRequestScan: () -> Unit) {
    AuraTheme {
        val nav = rememberNavController()
        val snackbar = remember { SnackbarHostState() }
        LaunchedEffect(scanMessage) { scanMessage?.let { snackbar.showSnackbar(it); onMessageShown() } }
        Scaffold(
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                Column {
                    PlayerMiniHost(onExpand = { nav.navigate("player") })
                    NavigationBar { val entry by nav.currentBackStackEntryAsState(); AuraRoute.entries.forEach { item -> val selected = entry?.destination?.hierarchy?.any { it.route == item.route } == true; NavigationBarItem(selected=selected, onClick={nav.navigate(item.route){popUpTo(nav.graph.startDestinationId){saveState=true}; launchSingleTop=true; restoreState=true}}, icon={Icon(item.icon,null)}, label={Text(item.label)}) } }
                }
            }
        ) { padding ->
            NavHost(navController = nav, startDestination = AuraRoute.Home.route, modifier = Modifier.padding(padding)) {
                composable(AuraRoute.Home.route) { HomeRoute(onOpenReplay = { nav.navigate("replay") }, onRequestScan = onRequestScan) }
                composable(AuraRoute.Library.route) { LibraryRoute(onRequestScan = onRequestScan) }
                composable(AuraRoute.Search.route) { SearchRoute() }
                composable(AuraRoute.Playlists.route) { PlaylistsRoute() }
                composable(AuraRoute.Settings.route) { SettingsRoute() }
                composable("downloads") { DownloadsRoute() }
                composable("player") { FullPlayerRoute(onClose = { nav.popBackStack() }) }
                composable("replay") { ReplayRoute(onBack = { nav.popBackStack() }) }
            }
        }
    }
}

@Composable
private fun ReplayRoute(onBack: () -> Unit) {
    androidx.compose.foundation.layout.Column(Modifier.fillMaxSize().padding(24.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, null) }
        Text("Aura Replay Report", style = MaterialTheme.typography.headlineLarge)
        Text("100% Offline Historical rollups are calculated from local playback history. As you listen, Aura builds total listening time, Hi-Res ratio, milestones, top genres, top artists and top songs.", modifier = Modifier.padding(top = 16.dp))
    }
}
