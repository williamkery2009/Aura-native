package ai.arena.aura.core.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AuraCyan = Color(0xFF00E5FF)
val AuraCrimson = Color(0xFFFF2D55)
val AuraGold = Color(0xFFFFD166)
val AuraBlack = Color(0xFF050507)
val AuraPanel = Color(0xFF141418)

enum class AuraVisualTheme { DarkGlass, Amoled, LightGlass }
enum class AuraVisualAccent { Cyan, Crimson, Gold }

@Composable
fun AuraTheme(theme: AuraVisualTheme = AuraVisualTheme.DarkGlass, accent: AuraVisualAccent = AuraVisualAccent.Cyan, content: @Composable () -> Unit) {
    val seed = when(accent){ AuraVisualAccent.Cyan -> AuraCyan; AuraVisualAccent.Crimson -> AuraCrimson; AuraVisualAccent.Gold -> AuraGold }
    val scheme = when(theme) {
        AuraVisualTheme.LightGlass -> lightColorScheme(primary = seed, secondary = seed, background = Color(0xFFF8F8FB), surface = Color.White)
        AuraVisualTheme.Amoled -> darkColorScheme(primary = seed, secondary = seed, background = Color.Black, surface = Color.Black, surfaceVariant = Color(0xFF08080A))
        AuraVisualTheme.DarkGlass -> darkColorScheme(primary = seed, secondary = seed, background = AuraBlack, surface = AuraPanel, surfaceVariant = Color(0xFF202027))
    }
    MaterialTheme(colorScheme = scheme, typography = AuraTypography, shapes = AuraShapes, content = content)
}

val AuraTypography = Typography()
val AuraShapes = Shapes(extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(10), small = androidx.compose.foundation.shape.RoundedCornerShape(14), medium = androidx.compose.foundation.shape.RoundedCornerShape(20), large = androidx.compose.foundation.shape.RoundedCornerShape(28), extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(36))
