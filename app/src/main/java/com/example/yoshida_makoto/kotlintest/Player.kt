package com.example.yoshida_makoto.kotlintest

import android.content.ContentUris
import android.content.Context
import android.media.MediaCodec
import android.media.PlaybackParams
import android.provider.MediaStore
import com.google.android.exoplayer.*
import com.google.android.exoplayer.extractor.ExtractorSampleSource
import com.google.android.exoplayer.upstream.DefaultAllocator
import com.google.android.exoplayer.upstream.DefaultUriDataSource

/**
 * Created by yoshida_makoto on 2016/10/25.
 */
class Player(val context: Context) : ExoPlayer.Listener {
    private val BUFFER_SEGMENT_SIZE = 64 * 1024
    private val BUFFER_SEGMENT_COUNT = 256
    private val EXOPLAYER_INITIAL_RENDER_SIZE = 2
    var audioRenderer: MediaCodecAudioTrackRenderer? = null
    val exoPlayer: ExoPlayer = ExoPlayer.Factory.newInstance(EXOPLAYER_INITIAL_RENDER_SIZE)
    var key: Float = 0.0f

    init {
        exoPlayer.playWhenReady = true
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayWhenReadyCommitted() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun sendChangePitchMessage(keyDifference: Int) {
        // ここでキー変えてる まじ大事なところ
        key += keyDifference
        val pitchFreq = generatePitchFrequency(key)
        val playbackParam = PlaybackParams().apply {
            pitch = pitchFreq
            speed = 1.0f
        }
        exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS, playbackParam)
    }

    fun generatePitchFrequency(key: Float): Float {
        return Math.pow(2.0, key.toDouble() / 12).toFloat()
    }

    fun playSong(songId: Long) {
        val allocator = DefaultAllocator(BUFFER_SEGMENT_SIZE)
        val trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

        val dataSource = DefaultUriDataSource(context, "userAgent")

        val sampleSource = ExtractorSampleSource(trackUri, dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE)

        val videoRenderer = MediaCodecVideoTrackRenderer(context, sampleSource,
                MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT)

        audioRenderer = MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT)

        val rendererArray = arrayOf<TrackRenderer>(videoRenderer, audioRenderer!!)

        exoPlayer.prepare(*rendererArray)
        exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS, null)
    }
}