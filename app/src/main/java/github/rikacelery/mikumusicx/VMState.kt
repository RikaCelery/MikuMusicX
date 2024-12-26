package github.rikacelery.mikumusicx

import androidx.compose.ui.graphics.Color
import github.rikacelery.mikumusicx.domain.Song
import github.rikacelery.mikumusicx.other.MusicControllerUiState
import github.rikacelery.mikumusicx.other.PlayerState

data class VMState(
    val dynamicColor: Boolean = true,
    val seedColor: Color = Color.Green,
    val darkMode: Int = 0,
    val musicControllerUiState: MusicControllerUiState = MusicControllerUiState(
        playerState = PlayerState.PAUSED,
        currentSong = null,
        currentPosition = 0,
        totalDuration = 0,
    ),
)
