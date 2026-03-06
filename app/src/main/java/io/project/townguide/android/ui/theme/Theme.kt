package io.project.townguide.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MoonlightBlue,
    secondary = MintGlow,
    tertiary = AquaMint,
    background = MidnightBlue,
    surface = MidnightSurface,
    surfaceVariant = MidnightSurface.copy(alpha = 0.85f),
    onPrimary = MidnightBlue,
    onSecondary = MidnightBlue,
    onBackground = PolarWhite,
    onSurface = PolarWhite,
    outline = FrostOutline,
    error = GlassError
)

private val LightColorScheme = lightColorScheme(
    primary = LagoonBlue,
    secondary = AuroraBlue,
    tertiary = AquaMint,
    background = PolarWhite,
    surface = MistWhite,
    surfaceVariant = PolarWhite,
    onPrimary = PolarWhite,
    onSecondary = PolarWhite,
    onTertiary = InkBlue,
    onBackground = InkBlue,
    onSurface = InkBlue,
    outline = FrostOutline,
    error = GlassError
)

@Composable
fun TownguideTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
