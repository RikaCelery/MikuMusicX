package github.rikacelery.mikumusicx.screen

import android.graphics.BitmapFactory
import android.graphics.Color.TRANSPARENT
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import github.rikacelery.mikumusicx.API
import github.rikacelery.mikumusicx.VM
import github.rikacelery.mikumusicx.component.Vinyl
import github.rikacelery.mikumusicx.other.MusicControllerUiState
import github.rikacelery.mikumusicx.other.PlayerState
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import github.rikacelery.mikumusicx.ui.theme.isDark
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

@Suppress("ktlint:standard:function-naming")
@Composable
fun PlayerScreen(
    id: Long,
    vm: VM = viewModel(),
    onNavigateUp: () -> Unit = {},
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.White) }
//    var cover by remember { mutableStateOf("") }
//    var musicControllerUiState by remember {
//        mutableStateOf(
//            MusicControllerUiState(
//                playerState = PlayerState.PAUSED,
//                currentSong = Song("", "", "", "", cover),
//                currentPosition = 0,
//                totalDuration = 0,
//            ),
//        )
//    }
    var exoPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var dataSource by remember { mutableStateOf<File?>(null) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val music = vm.songs.first { it.id == id }
            if (music.cover.isBlank()) {
                vm.updateMusic(id, music.copy(cover = API.fetchCover(id)))
            }
            API.client
                .prepareGet(vm.songs[vm.songs.indexOfFirst { it.id == id }].cover)
                .execute {
                    it.bodyAsChannel().toInputStream().use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val palette = Palette.from(bitmap).generate()
                        val d = palette.getDominantColor(TRANSPARENT)
                        dominantColor = Color(d)
                    }
                }
        }
        if (state.musicControllerUiState.currentSong?.id == id) {
            loading = false
            return@LaunchedEffect
        } else {
            vm.resetPlayer()
            withContext(Dispatchers.IO) {
                val file = context.cacheDir.resolve("$id.mp3")
                runCatching {
                    API.client.prepareGet("http://music.163.com/song/media/outer/url?id=$id.mp3")
                        .execute {
                            Log.i("DOWNLOAD", "Total ${it.headers[HttpHeaders.ContentLength]}")
                            file.outputStream().use { outputStream ->
                                val inputStream = it.bodyAsChannel().toInputStream()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    inputStream.transferTo(outputStream)
                                } else {
                                    val buffer = ByteArray(8192)
                                    var bytesRead: Int
                                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                        outputStream.write(buffer, 0, bytesRead)
                                    }

                                }
                            }
                            return@execute file
                        }
                }.onSuccess { it ->
                    dataSource = it
                }.onFailure {
                    Log.i("DOWNLOAD", "Failed to download $id")
                    it.printStackTrace()
                    if (file.exists()) {
                        dataSource = file
                        return@withContext
                    }
                    return@withContext
                }
            }
            Log.i("DOWNLOAD", "data $dataSource")
            if (dataSource == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "加载歌曲失败,为空", Toast.LENGTH_SHORT).show()
                }
                return@LaunchedEffect
            }
            var ee: Exception? = null
            for (i in 0..3) {
                try {
                    val fis = FileInputStream(dataSource)
                    vm.setPlayingSong(vm.songs.first { it.id == id }, fis.fd)
                    ee = null
                    break
                } catch (e: Exception) {
                    ee = e
                    e.printStackTrace()
                    delay(1000)
                    continue
                }
            }
            if (ee != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "加载歌曲失败\n$ee", Toast.LENGTH_LONG).show()
                }
                vm.resetPlayer()
                return@LaunchedEffect
            }
        }
    }
    LaunchedEffect(Unit) {
        launch {
            while (true) {
                if (vm.player.isPlaying)
                    vm.updatePosition()
                delay(1000)
            }
        }
    }
    SongScreenBody(
        song = state.musicControllerUiState.currentSong ?: return,
        dominantColor,
        {
            when (it) {
                is SongEvent.PauseSong -> {
                    vm.player.pause()
                    vm.setPlayingState(PlayerState.PAUSED)
                }

                is SongEvent.ResumeSong -> {
                    vm.player.start()
                    vm.setPlayingState(PlayerState.PLAYING)
                }

                is SongEvent.SeekSongToPosition -> {
                    vm.player.seekTo(it.position.toInt())
                    vm.seekTo(it.position)
                }

                is SongEvent.SkipToNextSong -> {}
                is SongEvent.SkipToPreviousSong -> {}
            }
        },
        state.musicControllerUiState,
        {
            exoPlayer?.release()
            onNavigateUp()
        },
    )
}

@Suppress("ktlint:standard:function-naming")
@PreviewLightDark
@Composable
fun PlayerScreenPreview() {
    MikuMusicXTheme {
        PlayerScreen(1)
    }
}

enum class SlideToActionAnchors {
    Start,
    End,
}

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongScreenBody(
    song: Music,
    dominantColor: Color = Color.Transparent,
    onEvent: (SongEvent) -> Unit,
    musicControllerUiState: MusicControllerUiState,
    onNavigateUp: () -> Unit,
) {
//    val swipeableState = rememberSwipeableState(initialValue = 0)
    val endAnchor = LocalConfiguration.current.screenHeightDp * LocalDensity.current.density
//    val anchors =
//        mapOf(
//            0f to 0,
//            endAnchor to 1,
//        )

    val backgroundColor = MaterialTheme.colorScheme.background

    val context = LocalContext.current

    val imagePainter =
        rememberAsyncImagePainter(
            model =
            ImageRequest
                .Builder(context)
                .data(song.cover)
                .crossfade(true)
                .build(),
        )

//    val iconResId =
    //        if (musicControllerUiState.playerState == PlayerState.PLAYING) R.drawable.ic_round_pause else R.drawable.ic_round_play_arrow

    val density = LocalDensity.current
    val anchors =
        with(density) {
            DraggableAnchors {
                SlideToActionAnchors.Start at -100.dp.toPx()
//                Center at 0f
                SlideToActionAnchors.End at 100.dp.toPx()
            }
        }

    val state =
        remember {
            AnchoredDraggableState(SlideToActionAnchors.Start)
        }
    SideEffect {
        state.updateAnchors(anchors)
    }
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .anchoredDraggable(state, orientation = Orientation.Vertical),
        //                .offset { IntOffset(x = state.requireOffset(), y = 0) },
//                .draggable(
//                    state = swipeableState,
//                    anchors = anchors,
//                    thresholds = { _, _ -> FractionalThreshold(0.34f) },
//                    orientation = Orientation.Vertical,
//                ),
    ) {
        if (state.currentValue > SlideToActionAnchors.Start) {
            LaunchedEffect(key1 = Unit) {
                onNavigateUp()
            }
        }
        SongScreenContent(
            song = song,
            isSongPlaying = musicControllerUiState.playerState == PlayerState.PLAYING,
            imagePainter = imagePainter,
            dominantColor = dominantColor,
            currentTime = musicControllerUiState.currentPosition,
            totalTime = musicControllerUiState.totalDuration,
            playOrToggleSong = {
                onEvent(if (musicControllerUiState.playerState == PlayerState.PLAYING) SongEvent.PauseSong else SongEvent.ResumeSong)
            },
            playNextSong = { onEvent(SongEvent.SkipToNextSong) },
            playPreviousSong = { onEvent(SongEvent.SkipToPreviousSong) },
            onSliderChange = { newPosition ->
                onEvent(SongEvent.SeekSongToPosition(newPosition.toLong()))
            },
            onForward = {
                onEvent(SongEvent.SeekSongToPosition(musicControllerUiState.currentPosition + 10 * 1000))
            },
            onRewind = {
                musicControllerUiState.currentPosition.let { currentPosition ->
                    onEvent(SongEvent.SeekSongToPosition(if (currentPosition - 10 * 1000 < 0) 0 else currentPosition - 10 * 1000))
                }
            },
            onClose = { onNavigateUp() },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@PreviewLightDark
@Composable
fun SongScreenPreview() {
    MikuMusicXTheme {
        val hct = Color.Green.toHct()
        var musicControllerUiState by remember {
            mutableStateOf(
                MusicControllerUiState(
                    playerState = PlayerState.PAUSED,
                    currentSong = Music(""),
                    currentPosition = 30,
                    totalDuration = 120,
                ),
            )
        }
        SongScreenBody(
            song = Music(""),
            Color.Transparent,
            {
                when (it) {
                    is SongEvent.PauseSong -> {
                        musicControllerUiState =
                            musicControllerUiState.copy(playerState = PlayerState.PAUSED)
                    }

                    is SongEvent.ResumeSong -> {
                        musicControllerUiState =
                            musicControllerUiState.copy(playerState = PlayerState.PLAYING)
                    }

                    is SongEvent.SeekSongToPosition -> {
                        musicControllerUiState =
                            musicControllerUiState.copy(currentPosition = it.position)
                    }

                    is SongEvent.SkipToNextSong -> {}
                    is SongEvent.SkipToPreviousSong -> {}
                }
            },
            musicControllerUiState,
            {
                println("")
            },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SongScreenContent(
    song: Music,
    isSongPlaying: Boolean,
    imagePainter: Painter,
    dominantColor: Color,
    currentTime: Long,
    totalTime: Long,
    playOrToggleSong: () -> Unit,
    playNextSong: () -> Unit,
    playPreviousSong: () -> Unit,
    onSliderChange: (Float) -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onClose: () -> Unit,
) {
    val hct = dominantColor.toHct()
    val dominantColor =
        if (isDark()) {
            Hct.from(hct.hue, hct.tone.coerceAtMost(30.0), 80.0).toColor()
        } else {
            Hct
                .from(
                    hct.hue,
                    hct.tone.times(2).coerceIn(30.0, 50.0),
                    (hct.tone * 1).coerceAtMost(50.0),
                ).toColor()
        }
    val gradientColors =
        if (isDark()) {
            listOf(
                Hct.from(hct.hue, hct.tone.coerceAtMost(50.0), hct.tone * 0.3).toColor(),
                MaterialTheme.colorScheme.background,
            )
        } else {
            listOf(
                Hct.from(hct.hue, hct.tone.coerceAtMost(30.0), 81.0).toColor(),
                MaterialTheme.colorScheme.background,
            )
        }

    val sliderColors =
        if (isDark()) {
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onBackground,
                activeTrackColor = MaterialTheme.colorScheme.onBackground,
                inactiveTrackColor =
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = .2f,
                ),
            )
        } else {
            SliderDefaults.colors(
                thumbColor = dominantColor,
                activeTrackColor = dominantColor,
                inactiveTrackColor =
                dominantColor.copy(
                    alpha = .2f,
                ),
            )
        }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Surface {
            Box(
                modifier =
                Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = gradientColors,
                            endY = LocalConfiguration.current.screenHeightDp.toFloat() * LocalDensity.current.density,
                        ),
                    )
                    .fillMaxSize(),
//                    .systemBarsPadding()
            ) {
                Column {
                    IconButton(
                        onClick = onClose,
                    ) {
                        Image(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Close",
                            colorFilter = ColorFilter.tint(LocalContentColor.current),
                        )
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier =
                            Modifier
                                .weight(1f, fill = true)
                                .height(IntrinsicSize.Max)
                                .width(IntrinsicSize.Max)
//                                    .fillMaxSize()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Vinyl(
                                modifier =
                                Modifier
                                    .padding(vertical = 32.dp)
                                    .fillMaxSize()
                                    .aspectRatio(1f)
                                    .shadow(10.dp, shape = MaterialTheme.shapes.large)
                                    .clip(MaterialTheme.shapes.large),
                                painter = imagePainter,
                                rotationDegrees = 0f,
                            )
                        }
                        Column {
                            Text(
                                text = song.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                song.artist,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier =
                                Modifier.graphicsLayer {
                                    alpha = 0.60f
                                },
                            )
                        }

                        Column(
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                        ) {
                            Slider(
                                value = currentTime.toFloat(),
                                modifier = Modifier.fillMaxWidth(),
                                valueRange = 0f..totalTime.toFloat(),
                                colors = sliderColors,
                                onValueChange = onSliderChange,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CompositionLocalProvider(
                                    LocalContentColor provides MaterialTheme.colorScheme.secondary.copy(
                                        alpha = .6f
                                    )
                                ) {
                                    Text(
                                        currentTime.toTime(),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                CompositionLocalProvider(
                                    LocalContentColor provides MaterialTheme.colorScheme.primary.copy(
                                        alpha = .6f
                                    )
                                ) {
                                    Text(
                                        totalTime.toTime(),
                                        style = MaterialTheme.typography.bodyMedium,

                                        )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipPrevious,
                                contentDescription = "Skip Previous",
                                modifier =
                                Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = playPreviousSong)
                                    .padding(12.dp)
                                    .size(32.dp),
                                tint = dominantColor,
                            )
                            Icon(
                                imageVector = Icons.Rounded.Replay10,
                                contentDescription = "Replay 10 seconds",
                                modifier =
                                Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onRewind)
                                    .padding(12.dp)
                                    .size(32.dp),
                                tint = dominantColor,
                            )
                            val rotateAnimated by remember { mutableStateOf(Animatable(30f)) }
                            val sizeAnimated by remember { mutableStateOf(Animatable(1f)) }
                            LaunchedEffect(isSongPlaying) {
                                launch {
                                    sizeAnimated.animateTo(0f, tween(durationMillis = 0))
                                    sizeAnimated.animateTo(
                                        1f,
                                        tween(durationMillis = 200, easing = EaseOutCirc),
                                    )
                                }
                                rotateAnimated.animateTo(-30f, tween(durationMillis = 0))
                                rotateAnimated.animateTo(
                                    0f,
                                    tween(durationMillis = 1000, easing = EaseOutElastic),
                                )
                            }
                            if (isSongPlaying) {
                                Icon(
                                    Icons.Rounded.Pause,
                                    contentDescription = "Play",
                                    tint = MaterialTheme.colorScheme.background,
                                    modifier =
                                    Modifier
                                        .clip(CircleShape)
                                        .rotate(rotateAnimated.value)
                                        .background(dominantColor)
                                        .clickable(onClick = playOrToggleSong)
                                        .size(64.dp)
                                        .padding(8.dp)
                                        .scale(sizeAnimated.value),
                                )
                            } else {
                                Icon(
                                    Icons.Rounded.PlayArrow,
                                    contentDescription = "Play",
                                    tint = MaterialTheme.colorScheme.background,
                                    modifier =
                                    Modifier
                                        .clip(CircleShape)
                                        .rotate(rotateAnimated.value)
                                        .background(dominantColor)
                                        .clickable(onClick = playOrToggleSong)
                                        .size(64.dp)
                                        .padding(8.dp)
                                        .scale(sizeAnimated.value),
                                )
                            }
                            Icon(
                                imageVector = Icons.Rounded.Forward10,
                                contentDescription = "Forward 10 seconds",
                                modifier =
                                Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onForward)
                                    .padding(12.dp)
                                    .size(32.dp),
                                tint = dominantColor,
                            )
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = "Skip Next",
                                modifier =
                                Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = playNextSong)
                                    .padding(12.dp)
                                    .size(32.dp),
                                tint = dominantColor,
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Long.toTime(): String {
    val stringBuffer = StringBuffer()

    val minutes = (this / 60000).toInt()
    val seconds = (this % 60000 / 1000).toInt()

    stringBuffer
        .append(String.format("%02d", minutes))
        .append(":")
        .append(String.format("%02d", seconds))

    return stringBuffer.toString()
}
