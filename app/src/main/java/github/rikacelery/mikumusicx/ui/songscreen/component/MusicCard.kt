package github.rikacelery.mikumusicx.ui.songscreen.component

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.rememberAsyncImagePainter
import coil3.toBitmap
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import github.rikacelery.mikumusicx.domain.other.getDominantColor
import github.rikacelery.mikumusicx.domain.other.getVibrantColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun MusicCard(
    name: String,
    artist: String,
    cover: String,
    modifier: Modifier = Modifier,
    dark: Boolean = isSystemInDarkTheme(),
) {
    ConstraintLayout(
        modifier
            .fillMaxWidth()
            .height(130.dp),
    ) {
        var vibrantColor by remember { mutableStateOf(if (dark) Color.White else Color.Black) }
        val tint by remember {
            derivedStateOf {
                vibrantColor
            }
        }
        val textColor by remember {
            derivedStateOf {
                val hct = vibrantColor.toHct()
                if (dark) {
                    Hct.from(hct.hue, hct.chroma, 90.0).toColor()
                } else {
                    Hct.from(hct.hue, hct.chroma, 10.0).toColor()
                }
            }
        }
        val gradients by remember {
            derivedStateOf {
                val hct = tint.toHct()
                if (dark) {
                    listOf(
                        Hct.from(hct.hue, hct.chroma.coerceAtMost(30.0), hct.tone * 0.3).toColor(),
                        Hct.from(hct.hue, hct.chroma.coerceAtMost(30.0), hct.tone * 0.3).toColor(),
                    )
                } else {
                    listOf(
                        Hct.from(hct.hue, hct.chroma.coerceAtMost(50.0), 81.0).toColor(),
                        Hct.from(hct.hue, hct.chroma.coerceAtMost(50.0), 68.0).toColor(),
                    )
                }
            }
        }
        val (imgRef, boxRef, infoRef) = createRefs()
        Image(
            painter =
            rememberAsyncImagePainter(
                cover.ifBlank { null },
                onError = {
                    Log.w("Image", "Error: ${it.result.throwable}")
                },
                onSuccess = {
                    val bitmap =
                        it.result.image
                            .toBitmap()
                            .copy(Bitmap.Config.ARGB_8888, false)
                    vibrantColor = getVibrantColor(bitmap)
                },
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
            Modifier
                .aspectRatio(1.4f)
                .constrainAs(imgRef) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
        )
        Canvas(
            Modifier
                .constrainAs(boxRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    val colors =
                        listOf(
                            Color.Black,
                            Color.Transparent,
                        )
                    drawContent()
                    drawRect(
                        brush =
                        Brush.linearGradient(
                            colors,
                            start =
                            Offset(
                                size.width - size.height - size.height * 0.2f,
                                0f,
                            ),
                            end =
                            Offset(
                                size.width - size.height + size.height * 0.6f,
                                size.width * 0.06f,
                            ),
                        ),
                        blendMode = BlendMode.DstIn,
                    )
                },
        ) {
            val brush =
                Brush.verticalGradient(
                    gradients,
                )
            drawRect(brush)
        }

        Column(
            Modifier
                .constrainAs(infoRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                name,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            Text(
                artist,
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}
