package es.timasostima.robank.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,

    // textfields, configs, home sections, charts topBar and lists
    onSurface = DarkOnSurface,
    surface = DarkSurface,

    onSecondaryContainer = DarkOnSecondaryContainer,
    secondaryContainer = DarkSecondaryContainer,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    background = LightBackground,

    onSurface = LightOnSurface,
    surface = LightSurface,

    onSecondaryContainer = LightOnSecondaryContainer.copy(alpha = 0.45f),
    secondaryContainer = LightSecondaryContainer.copy(alpha = 0.5f),
)

@Composable
fun RobankTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}