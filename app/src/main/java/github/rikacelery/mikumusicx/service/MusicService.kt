package github.rikacelery.mikumusicx.service

import android.media.MediaPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class MusicService : MediaSessionService() {
    companion object {
        var INS: MusicService? = null
    }

    private var mediaSession: MediaSession? = null

    lateinit var exoPlayer: ExoPlayer
    val player = MediaPlayer()

    override fun onCreate() {
        super.onCreate()
        INS = this
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        mediaSession =
            MediaSession
                .Builder(this, exoPlayer)
                .setCallback(MediaSessionCallback())
                .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        INS = null
        mediaSession?.run {
            exoPlayer.release()
            release()
            mediaSession = null
        }

        super.onDestroy()
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
        ): ListenableFuture<MutableList<MediaItem>> {
            val updatedMediaItems =
                mediaItems
                    .map {
                        it.buildUpon().setUri(it.mediaId).build()
                    }.toMutableList()
            return Futures.immediateFuture(updatedMediaItems)
        }
    }
}
