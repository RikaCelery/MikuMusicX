package github.rikacelery.mikumusicx.domain.usecase

import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.service.MusicController
import javax.inject.Inject

class SkipToNextSongUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke(updateHomeUi: (Song?) -> Unit) {
        musicController.skipToNextSong()
        musicController.prepare()
        updateHomeUi(musicController.getCurrentSong())
    }
}