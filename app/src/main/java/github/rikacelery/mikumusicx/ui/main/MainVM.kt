package github.rikacelery.mikumusicx.ui.main

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import github.rikacelery.mikumusicx.API
import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.other.FFTAudioProcessor
import github.rikacelery.mikumusicx.domain.usecase.AddMediaItemsUseCase
import github.rikacelery.mikumusicx.domain.usecase.GetPlaylistSizeUseCase
import github.rikacelery.mikumusicx.domain.usecase.PauseSongUseCase
import github.rikacelery.mikumusicx.domain.usecase.PlaySongUseCase
import github.rikacelery.mikumusicx.domain.usecase.ResumeSongUseCase
import github.rikacelery.mikumusicx.domain.usecase.SeekSongToPositionUseCase
import github.rikacelery.mikumusicx.domain.usecase.SetFFTCallback
import github.rikacelery.mikumusicx.domain.usecase.SetMediaItemsUseCase
import github.rikacelery.mikumusicx.domain.usecase.SkipToNextSongUseCase
import github.rikacelery.mikumusicx.domain.usecase.SkipToPreviousSongUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class MainVM @OptIn(UnstableApi::class)
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val setMediaItemsUseCase: SetMediaItemsUseCase,
    private val addMediaItemsUseCase: AddMediaItemsUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val getPlaylistSizeUseCase: GetPlaylistSizeUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val seekSongToPositionUseCase: SeekSongToPositionUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val musicRepository: MusicRepository,
    private val skipToPreviousSongUseCase: SkipToPreviousSongUseCase,
    private val skipToNextSongUseCase: SkipToNextSongUseCase,
    private val setFFTCallback: SetFFTCallback,
) : ViewModel() {
    var _mainUiState = MutableStateFlow(MainUiState())
    var fft by mutableFloatStateOf(0f)
    val mainUiState
        get() = _mainUiState

    //        private set
    val scope = CoroutineScope(SupervisorJob())

    val size = 4096
    val bands = 64
    val bandSize = size / bands
    val maxConst = 1750000000 //reference max value for accum magnitude
    var average = .0f

    //    fun drawAudio(canvas: Canvas): Canvas {
//        canvas.drawColor(Color.DKGRAY)
//        for (i in 0..bands - 1) {
//            var accum = .0f
//
//            synchronized(fft) {
//                for (j in 0..bandSize - 1 step 2) {
//                    //convert real and imag part to get energy
//                    accum += (Math.pow(fft[j + (i * bandSize)].toDouble(), 2.0) + Math.pow(fft[j + 1 + (i * bandSize)].toDouble(), 2.0)).toFloat()
//                }
//
//                accum /= bandSize / 2
//            }
//
//            average += accum
//
//            canvas.drawRect(width * (i / bands.toFloat()), height - (height * Math.min(accum / maxConst.toDouble(), 1.0).toFloat()) - height * .02f, width * (i / bands.toFloat()) + width / bands.toFloat(), height.toFloat(), paintBandsFill)
//            canvas.drawRect(width * (i / bands.toFloat()), height - (height * Math.min(accum / maxConst.toDouble(), 1.0).toFloat()) - height * .02f, width * (i / bands.toFloat()) + width / bands.toFloat(), height.toFloat(), paintBands)
//        }
//
//        average /= bands
//
//        canvas.drawLine(0f, height - (height * (average / maxConst)) - height * .02f, width.toFloat(), height - (height * (average / maxConst)) - height * .02f, paintAvg)
//        canvas.drawText("FFT BANDS", 16f.px, 24f.px, paintText)
//
//        return canvas
//    }
    init {
        setFFTCallback(object : FFTAudioProcessor.FFTListener {
            override fun onFFTReady(
                sampleRateHz: Int,
                channelCount: Int,
                fft: FloatArray
            ) {

                val pairs = mutableListOf<Float>();

                for (i in 0 until fft.size step 2) {
                    val real = fft[i];
                    val imaginary = fft[i + 1];
                    val energy = real.pow(2) + imaginary.pow(2)
                    pairs.add(energy);
                }
                val sum = pairs.subList(2, 30).sum() / 1750000
                Log.v("FFTVmCallback", "%.6f".format(sum))
                this@MainVM.fft = sum.coerceAtMost(1f)
            }

        })
        scope.launch(Dispatchers.IO) {
            musicRepository.getSongs().collect { songs ->
                _mainUiState.update {
                    it.copy(
                        songs = songs
                    )
                }
                println("MainVM updated")
            }
        }
    }

    //    var
    val client = HttpClient(OkHttp) {}
    fun play(i: Int) {
        scope.launch(Dispatchers.Main) {
            if (getPlaylistSizeUseCase() == 0) {
                set()
            }
            playSongUseCase(i)
        }
    }

    fun pause() {
        pauseSongUseCase()
    }

    fun seek(position: Long) {
        seekSongToPositionUseCase(position)
    }

    fun resume() {
        resumeSongUseCase()
    }

    fun previous() {
        skipToPreviousSongUseCase {
        }
    }

    fun next() {
        skipToNextSongUseCase {

        }
    }

    suspend fun set() {
        _mainUiState.update {
            it.copy(
                loading = true
            )
        }
        val mapped = _mainUiState.value.songs.asFlow().map { song ->
            val file = context.cacheDir.resolve("${song.mediaId}.mp3")
            if (!file.exists())
                client.prepareGet(song.songUrl).execute {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.bodyAsChannel().toInputStream().transferTo(file.outputStream())
                    } else {
                        val bytes = ByteArray(1024)
                        it.bodyAsChannel().toInputStream().use { input ->
                            file.outputStream().use { output ->
                                var len = 0
                                while (input.read(bytes)
                                        .also { len = it } != -1
                                ) output.write(bytes, 0, len)
                            }
                        }
                    }
                }
            song.copy(
                uri = file.toUri(),
            )
        }.toList()
        withContext(Dispatchers.Main) {
            setMediaItemsUseCase(mapped)
        }
        _mainUiState.update {
            it.copy(
                loading = false
            )
        }
    }

    fun addSong(song: Song) {
        if (_mainUiState.value.songs.any { it.mediaId == song.mediaId })
            return
        _mainUiState.update {
            it.copy(
                loading = true
            )
        }
        scope.launch {
            musicRepository.addSong(song)
            runCatching {
                val file = context.cacheDir.resolve("${song.mediaId}.mp3")
                if (!file.exists())
                    client.prepareGet(song.songUrl).execute {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            it.bodyAsChannel().toInputStream().transferTo(file.outputStream())
                        } else {
                            val bytes = ByteArray(1024)
                            it.bodyAsChannel().toInputStream().use { input ->
                                file.outputStream().use { output ->
                                    var len = 0
                                    while (input.read(bytes)
                                            .also { len = it } != -1
                                    ) output.write(bytes, 0, len)
                                }
                            }
                        }
                    }
                val song = song.copy(
                    uri = file.toUri(),
                )
                withContext(Dispatchers.Main) {
//                    mainUiState = mainUiState.copy(
//                        songs = mainUiState.songs.toMutableList().apply {
//                            add(song)
//                        }
//                    )
                    if (getPlaylistSizeUseCase() == 0) {
                        set()
                    } else {
                        addMediaItemsUseCase(listOf(song))
                    }
                }
            }

            _mainUiState.update {
                it.copy(
                    loading = false
                )
            }
        }

    }

    fun removeSong(mediaId: String) {
        musicRepository.removeSong(mediaId)
        scope.launch() {
            set()
        }
    }

    fun updateSong(mediaId: String) {
        if (!_mainUiState.value.songs.any { it.mediaId == mediaId })
            return
        scope.launch(Dispatchers.IO) {
            val song = API.fetchInfo(mediaId)
            _mainUiState.update {
                it.copy(
                    songs = it.songs.toMutableList().apply {
                        replaceAll {
                            if (it.mediaId == mediaId) {
                                song
                            } else it
                        }
                    }
                )
            }
        }
    }
}