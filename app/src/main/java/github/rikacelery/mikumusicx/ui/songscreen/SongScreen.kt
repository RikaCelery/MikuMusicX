package github.rikacelery.mikumusicx.ui.songscreen

import androidx.compose.runtime.Composable
import github.rikacelery.mikumusicx.domain.other.MusicControllerUiState
import github.rikacelery.mikumusicx.ui.songscreen.component.SimpleSongScreen


enum class SlideToActionAnchors {
    Start,
    End,
}

@Composable
fun SongScreen(
    onEvent: (SongEvent) -> Unit,
    musicControllerUiState: MusicControllerUiState,
    onNavigateUp: () -> Unit,
) {

    SimpleSongScreen(
        song = musicControllerUiState.currentSong ?: return,
        onEvent,
        musicControllerUiState,
        {
            onNavigateUp()
        },
    )
}


fun Long.toTime(): String {
    val stringBuffer = StringBuffer()

    val minutes = (this / 60000).toInt()
    val seconds = (this % 60000 / 1000).toInt()

    stringBuffer
        .append(String.format("%02d", minutes))
        .append(":")
        .append(String.format("%02d", seconds))

    return stringBuffer.toString()
}
