package github.rikacelery.mikumusicx.screen

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
import androidx.compose.foundation.gestures.snapping.SnapPosition.Center
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import github.rikacelery.mikumusicx.component.AnimatedVinyl
import github.rikacelery.mikumusicx.domain.Song
import github.rikacelery.mikumusicx.other.MusicControllerUiState
import github.rikacelery.mikumusicx.other.PlayerState
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun PlayerScreen(onNavigateUp: () -> Unit = {}) {
    Surface {
        Row(
            Modifier
                .fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterVertically,
        ) {
            var musicControllerUiState by remember {
                mutableStateOf(
                    MusicControllerUiState(
                        playerState = PlayerState.PAUSED,
                        currentSong = Song("", "", "", "", ""),
                        currentPosition = 30,
                        totalDuration = 120,
                    ),
                )
            }
            SongScreenBody(
                song = Song("", "", "", "", ""),
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
                onNavigateUp,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@PreviewLightDark
@Composable
fun PlayerScreenPreview() {
    MikuMusicXTheme {
        PlayerScreen()
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
    song: Song,
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

    val dominantColor by remember { mutableStateOf(Color.Transparent) }

    val context = LocalContext.current

    val imagePainter =
        rememberAsyncImagePainter(
            model =
                ImageRequest
                    .Builder(context)
                    .data(song.imageUrl)
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
                    currentSong = Song("", "", "", "", ""),
                    currentPosition = 30,
                    totalDuration = 120,
                ),
            )
        }
        SongScreenBody(
            song = Song("", "", "", "", ""),
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
    song: Song,
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
        if (isSystemInDarkTheme()) {
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
        if (isSystemInDarkTheme()) {
            listOf(
                Hct.from(hct.hue, hct.tone.coerceAtMost(50.0), hct.tone * 0.3).toColor(),
                MaterialTheme.colorScheme.background,
            )
        } else {
            listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
            )
        }

    val sliderColors =
        if (isSystemInDarkTheme()) {
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
                        ).fillMaxSize()
                        .systemBarsPadding(),
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
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .padding(vertical = 32.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .weight(1f, fill = false)
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                        ) {
                            AnimatedVinyl(painter = imagePainter, isSongPlaying = isSongPlaying)
                        }
                        Column(Modifier.weight(0.2f)) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                song.subtitle,
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
                                    .padding(vertical = 24.dp),
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
//                                CompositionLocalProvider(LocalContentColor provides ContentAlpha.medium) {
                                Text(
                                    currentTime.toTime(),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
//                                }
//                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text(
                                    totalTime.toTime(),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
//                                }
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
                                    sizeAnimated.animateTo(1f, tween(durationMillis = 200, easing = EaseOutCirc))
                                }
                                rotateAnimated.animateTo(-30f, tween(durationMillis = 0))
                                rotateAnimated.animateTo(0f, tween(durationMillis = 1000, easing = EaseOutElastic))
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

fun Long.toTime(): String = this.toString()
