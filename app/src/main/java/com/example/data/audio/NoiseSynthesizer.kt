package com.example.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.NoiseType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random

class NoiseSynthesizer {

    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private val random = Random()

    private val _currentNoise = MutableStateFlow(NoiseType.NONE)
    val currentNoise: StateFlow<NoiseType> = _currentNoise.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volume = MutableStateFlow(0.75f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val sampleRate = 44100
    private val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ).coerceAtLeast(4096)

    fun start(noiseType: NoiseType, customVolume: Float? = null) {
        if (noiseType == NoiseType.NONE) {
            stop()
            return
        }

        customVolume?.let { _volume.value = it.coerceIn(0f, 1f) }

        // If already playing the same noise, don't restart audio track
        if (_isPlaying.value && _currentNoise.value == noiseType) {
            return
        }

        stop()

        _currentNoise.value = noiseType
        _isPlaying.value = true

        synthJob = scope.launch {
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()

                val buffer = ShortArray(bufferSize)

                // Filter states for Pink Noise (Kellet algorithm)
                var b0 = 0.0f
                var b1 = 0.0f
                var b2 = 0.0f
                var b3 = 0.0f
                var b4 = 0.0f
                var b5 = 0.0f
                var b6 = 0.0f

                // State for Brown Noise (Leaky integration)
                var lastBrown = 0.0f

                while (isActive && _isPlaying.value) {
                    val vol = _volume.value
                    val type = _currentNoise.value

                    for (i in buffer.indices) {
                        val white = (random.nextFloat() * 2f - 1f)

                        val sample: Float = when (type) {
                            NoiseType.WHITE -> {
                                white * 0.35f
                            }
                            NoiseType.BROWN -> {
                                lastBrown = (lastBrown + (0.025f * white)) / 1.02f
                                (lastBrown * 3.2f).coerceIn(-1f, 1f)
                            }
                            NoiseType.PINK -> {
                                b0 = 0.99886f * b0 + white * 0.0555179f
                                b1 = 0.99332f * b1 + white * 0.0750759f
                                b2 = 0.96900f * b2 + white * 0.1538520f
                                b3 = 0.86650f * b3 + white * 0.3104856f
                                b4 = 0.55000f * b4 + white * 0.5329522f
                                b5 = -0.7616f * b5 - white * 0.0168980f
                                val pink = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362f) * 0.11f
                                b6 = white * 0.115926f
                                pink.coerceIn(-1f, 1f)
                            }
                            NoiseType.MIXED -> {
                                // 70% Pink + 30% Brown
                                b0 = 0.99886f * b0 + white * 0.0555179f
                                b1 = 0.99332f * b1 + white * 0.0750759f
                                b2 = 0.96900f * b2 + white * 0.1538520f
                                b3 = 0.86650f * b3 + white * 0.3104856f
                                b4 = 0.55000f * b4 + white * 0.5329522f
                                b5 = -0.7616f * b5 - white * 0.0168980f
                                val pink = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362f) * 0.11f
                                b6 = white * 0.115926f

                                lastBrown = (lastBrown + (0.025f * white)) / 1.02f
                                val brown = (lastBrown * 3.2f).coerceIn(-1f, 1f)

                                ((pink * 0.70f) + (brown * 0.30f)).coerceIn(-1f, 1f)
                            }
                            NoiseType.NONE -> 0f
                        }

                        val pcmValue = (sample * vol * 32767f).toInt().coerceIn(-32768, 32767)
                        buffer[i] = pcmValue.toShort()
                    }

                    track.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cleanUpTrack()
            }
        }
    }

    fun setVolume(vol: Float) {
        _volume.value = vol.coerceIn(0f, 1f)
    }

    fun stop() {
        _isPlaying.value = false
        _currentNoise.value = NoiseType.NONE
        synthJob?.cancel()
        synthJob = null
        cleanUpTrack()
    }

    private fun cleanUpTrack() {
        try {
            audioTrack?.let {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            audioTrack = null
        }
    }
}
