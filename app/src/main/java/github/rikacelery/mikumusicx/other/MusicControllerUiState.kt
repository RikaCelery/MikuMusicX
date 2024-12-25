package github.rikacelery.mikumusicx.other

import github.rikacelery.mikumusicx.domain.Song

data class MusicControllerUiState(
    val playerState: PlayerState? = null,
    val currentSong: Song? = null,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val isRepeatOneEnabled: Boolean = false,
)
