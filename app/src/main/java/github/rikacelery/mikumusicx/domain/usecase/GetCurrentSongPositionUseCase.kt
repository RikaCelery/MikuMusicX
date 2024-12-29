package github.rikacelery.mikumusicx.domain.usecase

import github.rikacelery.mikumusicx.domain.service.MusicController
import javax.inject.Inject

class GetCurrentSongPositionUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke() = musicController.getCurrentPosition()
}