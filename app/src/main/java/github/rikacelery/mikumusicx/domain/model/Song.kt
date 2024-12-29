package github.rikacelery.mikumusicx.domain.model

import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Song(
    val mediaId: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String="",
    val desc: String="",
    @Transient val uri: Uri?=null
) {

    val songUrl: String
        get() = "http://music.163.com/song/media/outer/url?id=${mediaId}.mp3"
}
