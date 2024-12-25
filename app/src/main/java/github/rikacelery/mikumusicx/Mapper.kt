package github.rikacelery.mikumusicx

import androidx.media3.common.MediaItem
import github.rikacelery.mikumusicx.domain.Song

fun MediaItem.toSong() =
    Song(
        mediaId = mediaId,
        title = mediaMetadata.title.toString(),
        subtitle = mediaMetadata.subtitle.toString(),
        songUrl = mediaId,
        imageUrl = mediaMetadata.artworkUri.toString(),
    )
