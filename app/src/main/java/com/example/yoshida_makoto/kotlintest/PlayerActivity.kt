package com.example.yoshida_makoto.kotlintest

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.media.MediaCodec
import android.media.PlaybackParams
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.yoshida_makoto.kotlintest.databinding.PlayerActivityBinding
import com.google.android.exoplayer.*
import com.google.android.exoplayer.extractor.ExtractorSampleSource
import com.google.android.exoplayer.upstream.DefaultAllocator
import com.google.android.exoplayer.upstream.DefaultUriDataSource

class PlayerActivity : AppCompatActivity(), ExoPlayer.Listener {
    private val BUFFER_SEGMENT_SIZE = 64 * 1024
    private val BUFFER_SEGMENT_COUNT = 256
    var audioRenderer: MediaCodecAudioTrackRenderer? = null
    var exoPlayer: ExoPlayer? = null
    var pitch: Float = 0.0f

    fun createIntent(context: Context, songId: Long): Intent {
        intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("song_id", songId)
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)


        val songId = intent.getLongExtra("song_id", 0)
        val allocator = DefaultAllocator(BUFFER_SEGMENT_SIZE)
        val trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

        val dataSource = DefaultUriDataSource(this, "userAgent")

        val sampleSource = ExtractorSampleSource(trackUri, dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE)
        val videoRenderer = MediaCodecVideoTrackRenderer(this, sampleSource,
                MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT)

        audioRenderer = MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT)

        val rendererArray = arrayOf<TrackRenderer>(videoRenderer, audioRenderer!!)

        exoPlayer = ExoPlayer.Factory.newInstance(rendererArray.size)
        (exoPlayer as ExoPlayer?)?.prepare(*rendererArray)
        (exoPlayer as ExoPlayer?)?.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS, null)
        (exoPlayer as ExoPlayer?)?.playWhenReady = true

        val binding = DataBindingUtil.setContentView<PlayerActivityBinding>(this, R.layout.player_activity);
        binding.pitchDown.setOnClickListener { sendChangePitchMessage(-1) }
        binding.pitchUp.setOnClickListener { sendChangePitchMessage(1) }
    }

    fun sendChangePitchMessage(key: Int) {
        // ここでキー変えてる まじ大事なところ
        pitch += key
        generatePitchFrequency(pitch)
        val freq = generatePitchFrequency(pitch)
        val playbackParam = PlaybackParams()
        playbackParam.pitch = freq
        playbackParam.speed = 1.0f // 毎回１をセットしないと、pitchに合わせて再生スピードが変わってしまう
        exoPlayer?.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS, playbackParam)
    }

    fun generatePitchFrequency(key: Float): Float {
        return Math.pow(2.0, key.toDouble() / 12).toFloat()
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        Log.d("デバッグ", "onPlayerError " + error.toString())
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d("デバッグ", "onPlayerStateChanged " + playWhenReady)
    }

    override fun onPlayWhenReadyCommitted() {
        Log.d("デバッグ", "onPlayWhenReadyCommitted")
    }
}
