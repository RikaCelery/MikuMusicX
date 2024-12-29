package github.rikacelery.mikumusicx.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.rememberDynamicColorScheme
import github.rikacelery.mikumusicx.ui.Settings

private val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40,
        /* Other default colors to override
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFFFFBFE),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
         */
    )

//@Composable
//fun isDark(viewModel: VM = viewModel()): Boolean {
//    val state by viewModel.uiState.collectAsState()
//    val dark =
//        when (state.darkMode) {
//            0 -> isSystemInDarkTheme()
//            1 -> false
//            2 -> true
//            else -> error("Invalid dark mode ${state.darkMode}")
//        }
//    return dark
//}

@Suppress("ktlint:standard:function-naming")
@Composable
fun MikuMusicXTheme(
    // Dynamic color is available on Android 12+
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val values: MutableList<ProvidedValue<out Any>> = mutableListOf()
    val colorScheme =
        when {
            // 使用动态取色
            Settings.dynamicColor -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val context = LocalContext.current
                    val theme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                        context
                    )
//                    val current = LocalDynamicMaterialThemeSeed.current
//                    val providedDynamicSeed =
//                        LocalDynamicMaterialThemeSeed provides if (!Settings.dynamicColor) current else Settings.seedColor
//                    values.add(providedDynamicSeed)
                    theme
                } else {
                    rememberDynamicColorScheme(
                        Settings.seedColor,
                        darkTheme,
                        true,
                    )
                }
            }
            // 不使用动态取色
            !Settings.dynamicColor ->
                // fallback

                rememberDynamicColorScheme(
                    Settings.seedColor,
                    darkTheme,
                    true,
                )

            else -> error("Invalid dark mode")
        }
    CompositionLocalProvider(*values.toTypedArray()){
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
            shapes = Shapes,
        )
    }
}
