package github.rikacelery.mikumusicx.domain.usecase

import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.service.MusicController
import javax.inject.Inject

class SkipToPreviousSongUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke(updateHomeUi: (Song?) -> Unit) {
        musicController.skipToPreviousSong()
        musicController.prepare()
        updateHomeUi(musicController.getCurrentSong())
    }
}