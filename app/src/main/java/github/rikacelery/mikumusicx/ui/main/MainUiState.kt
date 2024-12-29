package github.rikacelery.mikumusicx.ui.main

import github.rikacelery.mikumusicx.domain.model.Song

data class MainUiState(
    val songs: List<Song> = listOf(),
    val loading : Boolean = false,
    val currentSong : Song?=null,
)
