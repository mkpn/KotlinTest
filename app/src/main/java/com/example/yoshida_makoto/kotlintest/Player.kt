package com.example.yoshida_makoto.kotlintest

import android.content.ContentUris
import android.content.Context
import android.media.PlaybackParams
import android.os.Handler
import android.provider.MediaStore
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlaybackControlView
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

/**
 * Created by yoshida_makoto on 2016/10/25.
 */
class Player(val context: Context) : ExoPlayer.EventListener,
        TrackSelector.EventListener<MappingTrackSelector.MappedTrackInfo>,
        PlaybackControlView.VisibilityListener {

    val mainHandler = Handler()
    val defaultBandwidthMeter = DefaultBandwidthMeter()
    val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(defaultBandwidthMeter)
    val trackSelector = DefaultTrackSelector(mainHandler, videoTrackSelectionFactory)
    val loadControl: LoadControl = DefaultLoadControl()
    val exoPlayer: SimpleExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
    }
    var key: Float = 0.0f

    init {
        exoPlayer.playWhenReady = true
    }

    fun sendChangePitchMessage(keyDifference: Int) {
        // ここでキー変えてる まじ大事なところ
        key += keyDifference
        val pitchFreq = generatePitchFrequency(key)
        val playbackParams = PlaybackParams().apply {
            pitch = pitchFreq
            speed = 1.0f
        }
        exoPlayer.playbackParams = playbackParams
    }

    fun generatePitchFrequency(key: Float): Float {
        return Math.pow(2.0, key.toDouble() / 12).toFloat()
    }

    fun playSong(songId: Long) {

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getString(R.string.app_name)), defaultBandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory()
        val trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
        // This is the MediaSource representing the media to be played.
        val audioSource = ExtractorMediaSource(trackUri, dataSourceFactory, extractorsFactory, null, null)
        // Prepare the player with the source.
        exoPlayer.prepare(audioSource)
    }

    fun setPlayerView(playerView: SimpleExoPlayerView?) {
        playerView!!.player = exoPlayer
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPositionDiscontinuity() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTrackSelectionsChanged(trackSelections: TrackSelections<out MappingTrackSelector.MappedTrackInfo>?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onVisibilityChange(visibility: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}