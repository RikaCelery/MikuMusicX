package github.rikacelery.mikumusicx.component

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.palette.graphics.Palette
import coil3.compose.rememberAsyncImagePainter
import coil3.toBitmap
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import okhttp3.internal.toHexString

@Suppress("ktlint:standard:function-naming")
@Composable
fun MusicCard(
    name: String,
    artist: String,
    cover: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        ConstraintLayout(
            Modifier.Companion
                .fillMaxWidth()
                .height(100.dp),
        ) {
            var dominant by remember { mutableStateOf(Color.Companion.White) }
            var colorLight by remember { mutableStateOf(Color.Companion.Black) }
            var colorDark by remember { mutableStateOf(Color.Companion.Black) }
            val (imgRef, boxRef, infoRef) = createRefs()
            Image(
                painter =
                    rememberAsyncImagePainter(
                        cover,
                        onSuccess = {
                            val bitmap =
                                it.result.image
                                    .toBitmap()
                                    .copy(Bitmap.Config.ARGB_8888, false)
                            val palette = Palette.Builder(bitmap).generate()

                            val dominantColor =
                                Color(palette.getDominantColor(Color.Companion.Red.toArgb()))
                            Log.i(
                                "Palette",
                                "Color: ${
                                    dominantColor.toArgb()
                                        .toHexString()
                                }",
                            )
                            val hct = dominantColor.toHct()
                            dominant = Hct.Companion.from(hct.hue, 20.0, 85.0).toColor()
                            colorLight = Hct.Companion.from(hct.hue, 30.0, 25.0).toColor()
                            colorDark = Hct.Companion.from(hct.hue, 30.0, 5.0).toColor()
                        },
                    ),
                contentDescription = null,
                contentScale = ContentScale.Companion.Crop,
                modifier =
                    Modifier.Companion
                        .aspectRatio(1.3f)
                        .constrainAs(imgRef) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
            )
            Canvas(
                Modifier.Companion
                    .constrainAs(boxRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        width = Dimension.Companion.fillToConstraints
                        height = Dimension.Companion.fillToConstraints
                    }.graphicsLayer(compositingStrategy = CompositingStrategy.Companion.Offscreen)
                    .drawWithContent {
                        val colors =
                            listOf(
                                Color.Companion.Black,
                                Color.Companion.Transparent,
                            )
                        drawContent()
                        drawRect(
                            brush =
                                Brush.Companion.linearGradient(
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
                            blendMode = BlendMode.Companion.DstIn,
                        )
                    },
            ) {
                val brush =
                    Brush.Companion.verticalGradient(
                        listOf(
                            colorLight,
                            colorDark,
                        ),
                    )
                drawRect(brush)
            }

            Column(
                Modifier.Companion
                    .constrainAs(infoRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        width = Dimension.Companion.fillToConstraints
                        height = Dimension.Companion.fillToConstraints
                    }.padding(20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    name,
                    color = dominant,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    artist,
                    color = dominant,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
