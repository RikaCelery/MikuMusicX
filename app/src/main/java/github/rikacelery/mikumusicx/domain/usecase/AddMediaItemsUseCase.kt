package github.rikacelery.mikumusicx.domain.usecase

import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.service.MusicController
import javax.inject.Inject

class AddMediaItemsUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(songs: List<Song>) {
        songs.forEach {
            require(it.uri!=null)
        }
        println("add song "+songs.firstOrNull().toString())
        musicController.addMediaItems(songs)
    }
    init {
        println("AAAAAAAAAAAAAAAAAA")
    }
}