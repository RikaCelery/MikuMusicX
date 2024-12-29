package github.rikacelery.mikumusicx.ui.main

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.toBitmap
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import github.rikacelery.mikumusicx.API
import github.rikacelery.mikumusicx.R
import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.other.getVibrantColor
import github.rikacelery.mikumusicx.domain.other.transformDominant
import github.rikacelery.mikumusicx.ui.theme.MikuMusicXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun CurrentSongCard(
    song: Song,
    isSongPlaying: Boolean,
    modifier: Modifier = Modifier,
    albumScale : Float=0f,
    playOrToggleSong: () -> Unit = {},
    playPreviousSong: () -> Unit = {},
    playNextSong: () -> Unit = {},
) {
    var dominantColor by remember { mutableStateOf(Color.Transparent) }
    val darkTheme = isSystemInDarkTheme()
    val tint by remember {
        derivedStateOf {
            val hct = dominantColor.toHct()
            val res = if (darkTheme) {
                Hct.from(hct.hue, hct.tone.coerceAtMost(50.0), hct.tone * 0.3).toColor()
            } else {
                Hct.from(hct.hue, hct.tone.coerceAtMost(50.0), 81.0).toColor()
            }
            transformDominant(darkTheme,dominantColor)
            res
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background

    val context = LocalContext.current

    val hct = dominantColor.toHct()
    val gradientColors =
        if (darkTheme) {
            listOf(
                Hct.from(hct.hue, hct.tone.coerceAtMost(50.0), hct.tone * 0.7).toColor(),
                MaterialTheme.colorScheme.background,
            )
        } else {
            listOf(
                Hct.from(hct.hue, hct.tone.coerceAtMost(30.0), 91.0).toColor(),
                MaterialTheme.colorScheme.background,
            )
        }

    val sliderColors =
        if (darkTheme) {
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

    val imagePainter =
        rememberAsyncImagePainter(
            model =
            ImageRequest
                .Builder(context)
                .placeholder(R.drawable.ic_launcher_foreground)
                .data(song.imageUrl.ifBlank {
                    runBlocking(Dispatchers.IO) {
                        API.fetchCover(song.mediaId)
                    }
                })
                .crossfade(true)
                .build(),
            onSuccess = {
                val bitmap = it.result.image.toBitmap().copy(Bitmap.Config.ARGB_8888, true)
                dominantColor = getVibrantColor(bitmap)
            }
        )
    Surface(modifier.height(80.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Brush.linearGradient(gradientColors))
                .padding(8.dp)
                .fillMaxWidth(),
        ) {
            Image(
                imagePainter,
                "song vinyl",
                Modifier
                    .scale(1+albumScale.div(2))
                    .shadow(8.dp, shape = MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
                    .size(48.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(5.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    song.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                    Modifier.graphicsLayer {
                        alpha = 0.60f
                    },
                )
            }
            Spacer(Modifier.width(5.dp))
            Row(Modifier.width(128.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "Skip Previous",
                    modifier =
                    Modifier
                        .clip(CircleShape)
                        .clickable(onClick = playPreviousSong)
                        .size(32.dp),
                    tint = tint,
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
                Icon(
                    if (isSongPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.background,
                    modifier =
                    Modifier
                        .clip(CircleShape)
                        .rotate(rotateAnimated.value)
                        .background(tint)
                        .clickable(onClick = playOrToggleSong)
                        .size(48.dp)
                        .padding(8.dp)
                        .scale(sizeAnimated.value),
                )


                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Skip Next",
                    modifier =
                    Modifier
                        .clip(CircleShape)
                        .clickable(onClick = playNextSong)
                        .size(32.dp),
                    tint = tint,
                )
            }
        }
    }
}


@Composable
@PreviewLightDark()
@Preview(showBackground = true)
fun PreviewCurSongCard() {
    MikuMusicXTheme {
        Column(Modifier.height(80.dp)) {
            CurrentSongCard(
                Song(
                    "2052275194",
                    "PINK MOON",
                    "john",
                    "http://p2.music.126.net/M0tVPH1dzKV0IaxsT3sBPw==/109951168652004005.jpg"
                ), true
            )
        }
    }
}