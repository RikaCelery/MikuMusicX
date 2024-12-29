package github.rikacelery.mikumusicx.domain.other

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct


fun transformDominant(darkTheme: Boolean, dominantColor: Color): Color {
    val hct = dominantColor.toHct()
    return if (darkTheme) {
        Hct.from(hct.hue, hct.chroma.coerceAtMost(50.0), hct.tone * 0.3).toColor()
    } else {
        Hct.from(hct.hue, hct.chroma.coerceAtMost(50.0), 81.0).toColor()
    }
}

fun getVibrantColor(
    bitmap: Bitmap,
): Color {
    val palette =
        Palette.from(bitmap)
            .generate()
    return Color(
        palette.vibrantSwatch?.rgb
            ?: palette.dominantSwatch?.rgb
            ?: android.graphics.Color.YELLOW
    )
}

fun getDominantColor(
    bitmap: Bitmap,
): Color {
    val palette =
        Palette.from(bitmap)
            .generate()
    return Color(
        palette.dominantSwatch?.rgb
            ?: palette.vibrantSwatch?.rgb
            ?: android.graphics.Color.RED
    )
}