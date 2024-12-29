package github.rikacelery.mikumusicx.ui.songscreen.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Suppress("ktlint:standard:function-naming")
@Composable
fun AnimatedVinyl(
    modifier: Modifier = Modifier,
    isSongPlaying: Boolean = true,
    painter: Painter,
) {
    var currentRotation by remember {
        mutableFloatStateOf(0f)
    }

    val rotation =
        remember {
            Animatable(currentRotation)
        }

    LaunchedEffect(isSongPlaying) {
        if (isSongPlaying) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
            ) {
                currentRotation = value
            }
        } else {
            if (currentRotation > 0f) {
                rotation.animateTo(
                    targetValue = currentRotation + 50,
                    animationSpec =
                        tween(
                            1250,
                            easing = LinearOutSlowInEasing,
                        ),
                ) {
                    currentRotation = value
                }
            }
        }
    }

    Vinyl(modifier = modifier, painter = painter, rotationDegrees = rotation.value)
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun Vinyl(
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f,
    painter: Painter,
) {
    Box(
        modifier
    ) {
        // Vinyl background
        Image(
            modifier =
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .rotate(rotationDegrees)
            ,
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = "Song Cover",
        )

        // Vinyl song cover
//        Image(
//            modifier =
//                Modifier
//                    .fillMaxSize(0.5f)
//                    .rotate(rotationDegrees)
//                    .aspectRatio(1.0f)
//                    .align(Alignment.Center),
// //                    .clip(roundedShape)
//            painter = painter,
//            contentDescription = "Song cover",
//        )
    }
}
