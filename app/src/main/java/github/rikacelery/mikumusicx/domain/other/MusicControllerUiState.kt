package github.rikacelery.mikumusicx.domain.other

import github.rikacelery.mikumusicx.domain.model.Song

data class MusicControllerUiState(
    val playerState: PlayerState? = null,
    val currentSong: Song? = null,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val isRepeatOneEnabled: Boolean = false
) {
    fun pretty(): String {
        return "playerState: $playerState, currentSong: $currentSong, currentPosition: $currentPosition, totalDuration: $totalDuration, isShuffleEnabled: $isShuffleEnabled, isRepeatOneEnabled: $isRepeatOneEnabled"
    }
}
