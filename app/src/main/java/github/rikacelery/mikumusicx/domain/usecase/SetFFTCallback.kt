package github.rikacelery.mikumusicx.domain.usecase

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import github.rikacelery.mikumusicx.domain.other.FFTAudioProcessor
import javax.inject.Inject

@OptIn(UnstableApi::class)
class SetFFTCallback
@Inject constructor(
    private val fftAudioProcessor: FFTAudioProcessor,
) {
    operator fun invoke(vararg listener: FFTAudioProcessor.FFTListener) {
        fftAudioProcessor.listeners = listener

    }
}