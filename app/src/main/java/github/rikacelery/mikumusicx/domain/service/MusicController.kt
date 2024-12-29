package github.rikacelery.mikumusicx.domain.service

import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.other.PlayerState

interface MusicController {
    var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentMusic: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit
    )?

    fun addMediaItems(songs: List<Song>)
    fun setMediaItems(songs: List<Song>)

    fun play(mediaItemIndex: Int)
    fun prepare()

    fun resume()

    fun pause()

    fun getCurrentPosition(): Long

    fun destroy()

    fun skipToNextSong()

    fun skipToPreviousSong()

    fun getCurrentSong(): Song?

    fun seekTo(position: Long)
    fun playlistCount(): Int
}