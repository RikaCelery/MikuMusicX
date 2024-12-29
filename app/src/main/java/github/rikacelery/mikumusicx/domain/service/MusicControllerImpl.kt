package github.rikacelery.mikumusicx.domain.service

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.domain.repository.MusicRepository
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import github.rikacelery.mikumusicx.domain.model.Song
import github.rikacelery.mikumusicx.domain.other.PlayerState


class MusicControllerImpl(context: Context, musicRepository: MusicRepository) : MusicController {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    override var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentMusic: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit
    )? = null


    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            controllerListener()
//            GlobalScope.launch(Dispatchers.Main) {
//                mediaControllerFuture.get()!!.setMediaItems(
//                    musicRepository.getSongs().map { song ->
//                        val file = context.cacheDir.resolve("${song.mediaId}.mp3")
//                        song.copy(
//                            uri = file.toUri(),
//                        )
//                    }.map {
//                        MediaItem.Builder().setMediaMetadata(
//                            MediaMetadata.Builder().setTitle(it.title).setSubtitle(it.subtitle)
//                                .setArtworkUri(Uri.parse(it.imageUrl)).build()
//                        ).setMediaId(it.mediaId).setUri(it.uri).build()
//                    })
//            }

        }, MoreExecutors.directExecutor())
        println("BBBBBBBBBBBBB")
    }


    private fun controllerListener() {
        Log.i("MusicControllerImpl", "Add listeners, $mediaController")
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                with(player) {
                    println(currentMediaItem)

                    mediaControllerCallback?.invoke(
                        playbackState.toPlayerState(isPlaying),
                        currentMediaItem?.toSong(),
                        currentPosition.coerceAtLeast(0L),
                        duration.coerceAtLeast(0L),
                        shuffleModeEnabled,
                        repeatMode == Player.REPEAT_MODE_ONE
                    )
                }
            }
        })
    }

    private fun Int.toPlayerState(isPlaying: Boolean) =
        when (this) {
            Player.STATE_IDLE -> PlayerState.STOPPED
            Player.STATE_ENDED -> PlayerState.STOPPED
            else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
        }

    override fun addMediaItems(songs: List<Song>) {
        val mediaItems = songs.map {
            println("add " + it)
            MediaItem.Builder()
                .setMediaId(it.mediaId)
                .setUri(it.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setSubtitle(it.subtitle)
                        .setArtist(it.subtitle)
                        .setArtworkUri(Uri.parse(it.imageUrl))
                        .build()
                )
                .build()
        }
        mediaController?.addMediaItems(mediaItems)
    }

    override fun setMediaItems(songs: List<Song>) {
        val mediaItems = songs.map {
            println("set " + it)
            MediaItem.Builder()
                .setMediaId(it.mediaId)
                .setUri(it.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setSubtitle(it.subtitle)
                        .setArtist(it.subtitle)
                        .setArtworkUri(Uri.parse(it.imageUrl))
                        .build()
                )
                .build()
        }

        mediaController?.setMediaItems(mediaItems)
    }

    override fun play(mediaItemIndex: Int) {
        mediaController?.apply {
            seekToDefaultPosition(mediaItemIndex)
            playWhenReady = true
            prepare()
        }
    }

    override fun prepare() {
        mediaController?.prepare()
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L

    override fun getCurrentSong(): Song? = mediaController?.currentMediaItem?.toSong()
    override fun playlistCount(): Int = mediaController?.mediaItemCount?:0
    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaControllerCallback = null
    }

    override fun skipToNextSong() {
        mediaController?.seekToNext()
    }

    override fun skipToPreviousSong() {
        mediaController?.seekToPrevious()
    }

}

private fun MediaItem.toSong(): Song {
    Log.w("Ctrl", this.mediaId + this.mediaMetadata)
    return Song(
        mediaId,
        mediaMetadata.title.toString(),
        mediaMetadata.subtitle.toString(),
        mediaMetadata.artworkUri.toString()
    )
}
