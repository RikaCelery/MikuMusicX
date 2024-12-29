package github.rikacelery.mikumusicx.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.musicplayer.domain.repository.MusicRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import github.rikacelery.mikumusicx.data.musicData
import github.rikacelery.mikumusicx.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) :
    MusicRepository {
    private var _songs = mutableListOf<Song>()
    val Context.myDataStore by preferencesDataStore("songs")
    override suspend fun getSongs(): Flow<List<Song>> {
        val k = stringPreferencesKey("songs")
        return context.myDataStore.data.map {
            it[k]?.let { string -> Json.decodeFromString<List<Song>>(string) }
                ?: musicData

        }.onEach {
            println(it)
            _songs = it.toMutableList()
        }
    }

    override fun getDefaultSongs(): List<Song> {
        return musicData
    }

    suspend fun saveSongs() {
        runBlocking(Dispatchers.IO) {
            val k = stringPreferencesKey("songs")
            context.myDataStore.edit {
                it[k] = Json.encodeToString<List<Song>>(_songs)
            }
        }
    }

    override suspend fun addSong(song: Song) {
        if (!_songs.any { it.mediaId == song.mediaId }) {
            _songs.add(song)
        }
        saveSongs()
    }

    override fun removeSong(string: String) {
        runBlocking(Dispatchers.IO) {
            val k = stringPreferencesKey("songs")
            _songs.removeIf { it.mediaId == string }
            context.myDataStore.edit {
                it[k] = Json.encodeToString(_songs)
            }
        }
    }

}