package com.example.musicplayer.ui.songscreen

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import github.rikacelery.mikumusicx.domain.usecase.PauseSongUseCase
import github.rikacelery.mikumusicx.domain.usecase.ResumeSongUseCase
import github.rikacelery.mikumusicx.domain.usecase.SeekSongToPositionUseCase
import github.rikacelery.mikumusicx.domain.usecase.SkipToNextSongUseCase
import github.rikacelery.mikumusicx.domain.usecase.SkipToPreviousSongUseCase
import github.rikacelery.mikumusicx.ui.songscreen.SongEvent
import javax.inject.Inject

@HiltViewModel
class SongViewModel @OptIn(UnstableApi::class)
@Inject constructor(
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val skipToNextSongUseCase: SkipToNextSongUseCase,
    private val skipToPreviousSongUseCase: SkipToPreviousSongUseCase,
    private val seekSongToPositionUseCase: SeekSongToPositionUseCase,
) : ViewModel() {
    fun onEvent(event: SongEvent) {
        when (event) {
            SongEvent.PauseSong -> pauseMusic()
            SongEvent.ResumeSong -> resumeMusic()
            is SongEvent.SeekSongToPosition -> seekToPosition(event.position)
            SongEvent.SkipToNextSong -> skipToNextSong()
            SongEvent.SkipToPreviousSong -> skipToPreviousSong()
        }
    }

    private fun pauseMusic() {
        pauseSongUseCase()
    }

    private fun resumeMusic() {
        resumeSongUseCase()
    }

    private fun skipToNextSong() = skipToNextSongUseCase {

    }

    private fun skipToPreviousSong() = skipToPreviousSongUseCase {

    }

    private fun seekToPosition(position: Long) {
        seekSongToPositionUseCase(position)
    }


}
