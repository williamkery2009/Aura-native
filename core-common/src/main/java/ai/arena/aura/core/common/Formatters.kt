package ai.arena.aura.core.common

import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

object AuraFormatters {
    fun duration(ms: Long): String {
        val totalSeconds = (ms / 1000).coerceAtLeast(0)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) "%d:%02d:%02d".format(Locale.US, hours, minutes, seconds) else "%d:%02d".format(Locale.US, minutes, seconds)
    }

    fun fileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt().coerceIn(0, units.lastIndex)
        return "%.1f %s".format(Locale.US, bytes / 1024.0.pow(digitGroups), units[digitGroups])
    }
}
