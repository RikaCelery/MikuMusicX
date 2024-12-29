package github.rikacelery.mikumusicx.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.rikacelery.mikumusicx.domain.other.MusicControllerUiState
import github.rikacelery.mikumusicx.domain.other.PlayerState
import github.rikacelery.mikumusicx.domain.usecase.GetCurrentSongPositionUseCase
import github.rikacelery.mikumusicx.domain.usecase.SetMediaControllerCallbackUseCase
import github.rikacelery.mikumusicx.domain.usecase.SetMediaItemsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val setMediaControllerCallbackUseCase: SetMediaControllerCallbackUseCase,
    private val setMediaItemsUseCase: SetMediaItemsUseCase,
    private val getCurrentMusicPositionUseCase: GetCurrentSongPositionUseCase,
) : ViewModel() {
    var musicControllerUiState by mutableStateOf(MusicControllerUiState())
        private set

    init {
        setMediaControllerCallback()
    }

    private fun setMediaControllerCallback() {
        Log.i("SharedVM", "Init")
        setMediaControllerCallbackUseCase { playerState, currentSong, currentPosition, totalDuration,
                                            isShuffleEnabled, isRepeatOneEnabled ->
            musicControllerUiState = musicControllerUiState.copy(
                playerState = playerState,
                currentSong = currentSong,
                currentPosition = currentPosition,
                totalDuration = totalDuration,
                isShuffleEnabled = isShuffleEnabled,
                isRepeatOneEnabled = isRepeatOneEnabled
            )
            Log.i("SharedVM", musicControllerUiState.toString())

            if (playerState == PlayerState.PLAYING) {
                viewModelScope.launch {
                    while (isActive) {
                        delay(1.seconds)
                        musicControllerUiState = musicControllerUiState.copy(
                            currentPosition = getCurrentMusicPositionUseCase()
                        )
                    }
                }
            }


        }
    }

}