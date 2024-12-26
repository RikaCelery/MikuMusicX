package github.rikacelery.mikumusicx.other

import github.rikacelery.mikumusicx.domain.Song
import github.rikacelery.mikumusicx.screen.Music

data class MusicControllerUiState(
    val playerState: PlayerState? = null,
    val currentSong: Music? = null,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val isRepeatOneEnabled: Boolean = false,
    val loading:Boolean = true,
)
