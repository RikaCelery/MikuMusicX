package github.rikacelery.mikumusicx

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Settings

@Serializable
object MusicList

@Serializable
data class Player(
    val id: Long,
)
