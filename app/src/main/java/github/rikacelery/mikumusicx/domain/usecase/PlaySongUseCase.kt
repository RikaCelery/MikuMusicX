package github.rikacelery.mikumusicx.domain.usecase

import github.rikacelery.mikumusicx.domain.service.MusicController
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(private  val musicController: MusicController) {
    operator fun invoke(mediaItemIndex: Int) {
        println("play ${mediaItemIndex}")
        musicController.play(mediaItemIndex)
    }
}