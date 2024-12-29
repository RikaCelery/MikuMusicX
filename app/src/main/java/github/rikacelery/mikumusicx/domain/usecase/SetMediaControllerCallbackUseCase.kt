package github.rikacelery.mikumusicx.domain.usecase;

import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.other.PlayerState
import github.rikacelery.mikumusicx.domain.service.MusicController
import javax.inject.Inject

class SetMediaControllerCallbackUseCase @Inject constructor(
    val musicController: MusicController
) {
    operator fun invoke(
        callback: (
            playerState: PlayerState, currentSong: Song?, currentPosition: Long, totalDuration: Long, isShuffleEnabled: Boolean, isRepeatOneEnabled: Boolean
        ) -> Unit
    ) {
        musicController.mediaControllerCallback = callback
    }
}