import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.audio.TeeAudioProcessor
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import java.nio.ByteBuffer
import java.util.LinkedList
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

@SuppressLint("UnsafeOptInUsageError")
@UnstableApi
object RmsPeak : TeeAudioProcessor.AudioBufferSink {
    var value by mutableStateOf(0f)
    var windowLength = 30
    private val window = LinkedList<Double>()
    private var windowSum: Double = 0.0

    override fun flush(sampleRateHz: Int, channelCount: Int, encoding: Int) {
//        window.clear()
//        delayQueue.clear()
//        value = 0f
//        peak = 0f
    }

    override fun handleBuffer(buffer: ByteBuffer) {
        val samples = ByteArray(buffer.remaining())
        buffer.get(samples)

        for (sample in samples) {
            val sampleValue = sample / 128.0 // Normalize to range [-1.0, 1.0]
            val rms = sin(abs(sampleValue) * PI.div(2))
            if (rms.isFinite().not()) {
                continue
            }
            // Update the window for smoothing
            window.add(rms)
            windowSum += rms
            if (window.size > windowLength) {
                windowSum -= window.removeFirst()
            }

            // Calculate the smoothed RMS value
            value = (windowSum / windowLength).toFloat().takeIf { it.isFinite() } ?: return

            // Implement delay
//            delayQueue.add(value)
//            if (delayQueue.size > delaySamples) {
//                value = delayQueue.removeFirst()
//            }
        }
    }

//    fun setDelay(milliseconds: Int, sampleRateHz: Int) {
//        delaySamples = (milliseconds * sampleRateHz / 1000).toInt()
//    }
}

@OptIn(UnstableApi::class)
class MyRenderersFactory(
    context: Context,
    vararg val processors: AudioProcessor
) :
    DefaultRenderersFactory(context) {

    override fun buildAudioRenderers(
        context: Context,
        extensionRendererMode: Int,
        mediaCodecSelector: MediaCodecSelector,
        enableDecoderFallback: Boolean,
        audioSink: AudioSink,
        eventHandler: android.os.Handler,
        eventListener: AudioRendererEventListener,
        out: java.util.ArrayList<Renderer>
    ) {
//        val audioProcessor = arrayOf<AudioProcessor>(
//            TeeAudioProcessor(RmsPeak),
//            FFTAudioProcessor()
//        )

        out.add(
            MediaCodecAudioRenderer(
                context,
                mediaCodecSelector,
                enableDecoderFallback,
                eventHandler,
                eventListener,
                DefaultAudioSink.Builder(context)
                    .setAudioProcessors(processors)
                    .build()
            )
        )

        super.buildAudioRenderers(
            context,
            extensionRendererMode,
            mediaCodecSelector,
            enableDecoderFallback,
            audioSink,
            eventHandler,
            eventListener,
            out
        )
    }
}