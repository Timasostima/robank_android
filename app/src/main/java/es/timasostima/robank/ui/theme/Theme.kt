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

//private val DarkColorScheme = darkColorScheme(
//    primary = Green,
//    background = DarkBlue,
//
//    onSurface = White,
//    onSecondaryContainer = White.copy(alpha = 0.45f),
//
//    //textfields, configs, home sections, charts topBar and lists
//    surface = DarkerBlue,
//
////    surfaceContainer = LightBlue,
//
//    //navbar selection
//    secondaryContainer = White.copy(alpha = 0.15f),
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Green,
//    background = White,
//
//    onSurface = Black,
//    onSecondaryContainer = DarkBlue.copy(alpha = 0.45f),
//
//    surface = Whiter,
//
////    surfaceContainer = Gray,
//
//    secondaryContainer = DarkBlue.copy(alpha = 0.10f),
//
////    background = Color(0xFFFFFBFE),
////    surface = Color(0xFFFFFBFE),
////    onPrimary = Color.White,
////    onSecondary = Color.White,
////    onTertiary = Color.White,
////    onBackground = Color(0xFF1C1B1F),
////    onSurface = Color(0xFF1C1B1F),
//
//)

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
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        */

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}