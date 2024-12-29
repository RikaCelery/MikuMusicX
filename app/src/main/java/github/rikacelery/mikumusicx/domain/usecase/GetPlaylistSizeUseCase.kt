package github.rikacelery.mikumusicx.domain.usecase

import github.rikacelery.mikumusicx.domain.service.MusicController
import javax.inject.Inject

class GetPlaylistSizeUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke() = musicController.playlistCount()
}