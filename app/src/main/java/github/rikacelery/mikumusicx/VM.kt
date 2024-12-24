package github.rikacelery.mikumusicx

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toOkioPath
import javax.inject.Inject

@HiltViewModel
class VM
    @Inject
    constructor(
        @ApplicationContext val context: Context,
    ) : ViewModel() {
        val imageLoader =
            ImageLoader
                .Builder(context)
                .memoryCache {
                    MemoryCache
                        .Builder()
                        .maxSizePercent(context, 0.25)
                        .build()
                }.diskCache {
                    DiskCache
                        .Builder()
                        .directory(context.cacheDir.resolve("image_cache").toOkioPath())
                        .maxSizePercent(0.02)
                        .build()
                }.build()

        @Suppress("ktlint:standard:property-naming")
        fun save(dataStore: DataStore<Preferences>) {
            val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
            val SEED_COLOR = intPreferencesKey("seed_color")
            val DARK_MODE = intPreferencesKey("dark_mode")
            runBlocking {
                dataStore.edit {
                    it[DYNAMIC_COLOR] = _uiState.value.dynamicColor
                    it[SEED_COLOR] = _uiState.value.seedColor.toArgb()
                    it[DARK_MODE] = _uiState.value.darkMode
                }
            }
        }

        @Suppress("ktlint:standard:property-naming")
        suspend fun load(dataStore: DataStore<Preferences>) {
            val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
            val SEED_COLOR = intPreferencesKey("seed_color")
            val DARK_MODE = intPreferencesKey("dark_mode")
            try {
                dataStore.data.collect { value ->
                    _uiState.update { state ->
                        state.copy(
                            dynamicColor = value[DYNAMIC_COLOR] ?: true,
                            seedColor = Color(value[SEED_COLOR] ?: (0xFF0000FF).toInt()),
                            darkMode = value[DARK_MODE] ?: 0,
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun setSeedColor(color: Color) {
            _uiState.update { state ->
                state.copy(seedColor = color)
            }
        }

        fun setDarkMode(i: Int) {
            _uiState.update {
                it.copy(darkMode = i)
            }
        }

        fun setDynamicColor(bool: Boolean) {
            _uiState.update {
                it.copy(dynamicColor = bool)
            }
        }

        private val _uiState = MutableStateFlow(VMState())
        val uiState = _uiState.asStateFlow()
    }
