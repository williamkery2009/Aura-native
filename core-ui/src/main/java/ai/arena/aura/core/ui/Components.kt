package ai.arena.aura.core.ui

import ai.arena.aura.core.common.AuraFormatters
import ai.arena.aura.core.domain.model.Song
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = content
    )
}

@Composable
fun AlbumArt(
    uri: String?,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                        Color.Black
                    )
                )
            )
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = "Artwork cover for $title",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        Text(
            text = title.take(2).uppercase(),
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun SongRow(
    song: Song,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .semantics { contentDescription = "Song ${song.title} by ${song.artist}" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumArt(song.artworkUri, song.title, Modifier.size(52.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${song.artist} • ${song.format}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = AuraFormatters.duration(song.durationMs),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall
        )
        trailing()
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        action?.invoke()
    }
}

@Composable
fun EmptyAuraState(
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.LibraryMusic,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )
        Text(
            text = body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
        if (actionLabel != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun SpectrumBars(
    modifier: Modifier = Modifier,
    active: Boolean = true
) {
    val t by produceState(0f) {
        while (true) {
            value += 0.08f
            delay(16)
        }
    }
    Canvas(modifier) {
        val bars = 48
        val gap = 3.dp.toPx()
        val barWidth = (size.width - gap * (bars - 1)) / bars
        for (index in 0 until bars) {
            val level = if (active) 0.25f + 0.75f * abs(sin(t + index * 0.33f)) else 0.18f
            val barHeight = size.height * level
            val x = index * (barWidth + gap) + barWidth / 2f
            drawLine(
                color = Color(0xFF00E5FF),
                start = Offset(x, size.height),
                end = Offset(x, size.height - barHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    onToggle: () -> Unit,
    onExpand: () -> Unit,
    onNext: () -> Unit
) {
    LiquidGlassCard(
        Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = onExpand)
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumArt(song.artworkUri, song.title, Modifier.size(50.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = song.artist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Next"
                )
            }
        }
    }
}
