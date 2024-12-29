package github.rikacelery.mikumusicx.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import github.rikacelery.mikumusicx.domain.model.Song
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Settings {
    var seedColor by mutableStateOf(Color.Transparent)
    var dynamicColor by mutableStateOf(true)
    var darkMode by mutableIntStateOf(0)
    var songs = mutableListOf<Song>()
    fun load(context: Context) {
        GlobalScope.launch {
            context.prefs.data.collect {
                seedColor = Color(it[keySeed] ?: Color.Transparent.toArgb())
                dynamicColor = it[keyDynamicColor] ?: true
                darkMode = it[keyDarkMode] ?: 0
            }
        }
    }

    private val keySeed = intPreferencesKey("seed_color")
    private val keyDynamicColor = booleanPreferencesKey("dynamic_color")
    private val keyDarkMode = intPreferencesKey("dark_mode")
    private val Context.prefs by preferencesDataStore("settings")
    fun save(context: Context) {
        GlobalScope.launch() {
            context.prefs.edit {
                it[keySeed] = seedColor.toArgb()
                it[keyDynamicColor] = dynamicColor
                it[keyDarkMode] = darkMode
            }
        }
    }

//    @Composable
//    fun isDark(): Boolean {
//        return when (darkMode) {
//            0 -> isSystemInDarkTheme()
//            1 -> false
//            2 -> true
//            else -> false
//        }
//    }
}