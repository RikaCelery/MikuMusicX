package github.rikacelery.mikumusicx

import androidx.compose.ui.graphics.Color
import github.rikacelery.mikumusicx.domain.Song

data class VMState(
    val dynamicColor: Boolean = true,
    val seedColor: Color = Color.Green,
    val darkMode: Int = 0,
    val currentSong: Song? = null,
)
