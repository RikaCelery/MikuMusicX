package github.rikacelery.mikumusicx.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.materialkolor.rememberDynamicColorScheme
import github.rikacelery.mikumusicx.VM

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

@Suppress("ktlint:standard:function-naming")
@Composable
fun MikuMusicXTheme(
    // Dynamic color is available on Android 12+
    viewModel: VM = viewModel(),
    content: @Composable () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val dark =
        when (state.darkMode) {
            0 -> isSystemInDarkTheme()
            1 -> false
            2 -> true
            else -> error("Invalid dark mode ${state.darkMode}")
        }
    val colorScheme =
        when {
            // 使用动态取色
            state.dynamicColor -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val context = LocalContext.current
                    if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                } else {
                    // fallback
                    if (dark) DarkColorScheme else LightColorScheme
                }
            }
            // 不使用动态取色
            !state.dynamicColor ->
                rememberDynamicColorScheme(
                    state.seedColor,
                    dark,
                    true,
                )
            else -> error("Invalid dark mode ${state.darkMode}")
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = Shapes,
    )
}
