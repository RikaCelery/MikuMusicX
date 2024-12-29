package github.rikacelery.mikumusicx.domain.usecase

import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.service.MusicController
import javax.inject.Inject

class SetMediaItemsUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(songs: List<Song>) {
        println("set song "+songs.firstOrNull().toString())
        musicController.setMediaItems(songs)
        musicController.prepare()
    }
}