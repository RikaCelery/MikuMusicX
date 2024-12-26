package github.rikacelery.mikumusicx.component

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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil3.compose.rememberAsyncImagePainter
import coil3.toBitmap
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import github.rikacelery.mikumusicx.VM
import okhttp3.internal.toHexString

@Suppress("ktlint:standard:function-naming")
@Composable
fun MusicCard(
    name: String,
    artist: String,
    cover: String,
    modifier: Modifier = Modifier,
    viewModel: VM = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val dark =
        when (state.darkMode) {
            0 -> isSystemInDarkTheme()
            1 -> false
            2 -> true
            else -> error("Invalid dark mode ${state.darkMode}")
        }
    ElevatedCard( modifier = modifier) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .height(100.dp),
        ) {
            var dominant by remember { mutableStateOf(if (dark) Color.White else Color.DarkGray) }
            var colorLight by remember { mutableStateOf(if (dark) Color.DarkGray else Color.White) }
            var colorDark by remember { mutableStateOf(if (dark) Color.DarkGray else Color.White) }
            val (imgRef, boxRef, infoRef) = createRefs()
            Image(
                painter =
                rememberAsyncImagePainter(
                    cover.ifBlank { null },
                    imageLoader = viewModel.loader(LocalContext.current),
                    onError = {
                        Log.w("Image", "Error: ${it.result.throwable}")
                    },
                    onSuccess = {
                        val bitmap =
                            it.result.image
                                .toBitmap()
                                .copy(Bitmap.Config.ARGB_8888, false)
                        val palette = Palette.Builder(bitmap).generate()

                        val dominantColor =
                            Color(palette.getDominantColor(Color.Red.toArgb()))

                        val hct = dominantColor.toHct()
                        if (dark) {
                            dominant = Hct.from(hct.hue, 70.0, 95.0).toColor()
                            colorLight = Hct.from(hct.hue, 30.0, 25.0).toColor()
                            colorDark = Hct.from(hct.hue, 30.0, 5.0).toColor()
                        } else {
                            dominant = Hct.from(hct.hue, 70.0, 5.0).toColor()
                            colorLight = Hct.from(hct.hue, 30.0, 95.0).toColor()
                            colorDark = Hct.from(hct.hue, 30.0, 95.0).toColor()
                        }
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
                        listOf(
                            colorLight,
                            colorDark,
                        ),
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
                    color = dominant,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    artist,
                    color = dominant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
