package github.rikacelery.mikumusicx

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
//import github.rikacelery.mikumusicx.domain.Song
import github.rikacelery.mikumusicx.other.PlayerState
import github.rikacelery.mikumusicx.screen.Music
import github.rikacelery.mikumusicx.screen.musicData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okio.Path.Companion.toOkioPath
import java.io.FileDescriptor

class VM : ViewModel() {
    private var _loader: ImageLoader? = null
    lateinit var player: MediaPlayer
    private val _songs = mutableStateListOf<Music>()

    init {
        Log.d("VM", "jhkjhakfhj")
        //print stack
        val stackTrace = Thread.currentThread().stackTrace
        for (i in stackTrace.indices) {
            Log.d(
                "VM",
                "${stackTrace[i].className}.${stackTrace[i].methodName}:${stackTrace[i].lineNumber}"
            )
        }

        player = MediaPlayer()
    }

    fun loader(context: Context): ImageLoader {
        if (_loader != null) return _loader!!
        val l =
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
        _loader = l
        return l
    }

    @Suppress("ktlint:standard:property-naming")
    fun save(dataStore: DataStore<Preferences>) {
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val SEED_COLOR = intPreferencesKey("seed_color")
        val DARK_MODE = intPreferencesKey("dark_mode")
        val keySongs = stringPreferencesKey("songs")
        runBlocking {
            dataStore.edit {
                it[DYNAMIC_COLOR] = _uiState.value.dynamicColor
                it[SEED_COLOR] = _uiState.value.seedColor.toArgb()
                it[DARK_MODE] = _uiState.value.darkMode
                it[keySongs] = Json.encodeToString<List<Music>>(songs)
            }
        }
    }

    @Suppress("ktlint:standard:property-naming")
    suspend fun load(dataStore: DataStore<Preferences>) {
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val SEED_COLOR = intPreferencesKey("seed_color")
        val DARK_MODE = intPreferencesKey("dark_mode")
        val keySongs = stringPreferencesKey("songs")

        try {
            dataStore.data.collect { value ->
                _songs.addAll(value[keySongs]?.runCatching { Json.decodeFromString<List<Music>>(this) }
                    ?.getOrNull() ?: musicData)
                if (_songs.size == 0) {
                    _songs.addAll(musicData)
                }
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

    fun resetPlayer() {
        player.reset()
        _uiState.update { state ->
            state.copy(
                musicControllerUiState = state.musicControllerUiState.copy(
                    currentSong = null,
                    loading = true,
                    playerState = PlayerState.PAUSED
                ),
            )
        }

    }

    fun setPlayingSong(song: Music, data: FileDescriptor) {
        _uiState.update { state ->
            state.copy(
                musicControllerUiState = state.musicControllerUiState.copy(
                    currentSong = song,
                    playerState = PlayerState.PAUSED
                ),
            )
        }
        player.reset()
        player.setDataSource(data)
        player.prepare()
        player.setOnCompletionListener {
            setPlayingState(PlayerState.STOPPED)
        }
        player.setOnPreparedListener {
            setSongTotal(player.duration.toLong())
            updatePosition()
            _uiState.update { state ->
                state.copy(
                    musicControllerUiState = state.musicControllerUiState.copy(
                        loading = false
                    ),
                )
            }
        }
    }

    fun setSongTotal(length: Long) {
        _uiState.update { state ->
            state.copy(
                musicControllerUiState = state.musicControllerUiState.copy(
                    totalDuration = length
                ),
            )
        }
    }

    fun updatePosition() {
        _uiState.update { state ->
            state.copy(
                musicControllerUiState = state.musicControllerUiState.copy(
                    currentPosition = player.currentPosition.toLong()
                ),
            )
        }
    }

    fun setPlayingState(playState: PlayerState) {
        _uiState.update { state ->
            state.copy(
                musicControllerUiState = state.musicControllerUiState.copy(
                    playerState = playState
                ),
            )
        }
    }

    fun seekTo(position: Long) {
        _uiState.update { state ->
            state.copy(
                musicControllerUiState = state.musicControllerUiState.copy(
                    currentPosition = position
                ),
            )
        }
    }

    private val _uiState = MutableStateFlow(VMState())
    val uiState = _uiState.asStateFlow()
    val songs: SnapshotStateList<Music>
        get() = _songs

    fun addSong(song: Music) {
        if (_songs.any { it.id == song.id }) return
        _songs.add(song)
    }

    fun removeSong(id: Long) {
        _songs.removeIf { it.id == id }
    }

    fun updateMusic(id: Long, info: Music) {
        _songs[_songs.indexOfFirst { it.id == id }] = info
    }
}
