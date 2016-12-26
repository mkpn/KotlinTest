package com.example.yoshida_makoto.kotlintest

import android.content.Context
import android.media.PlaybackParams
import android.os.Handler
import android.util.Log
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.value.PlayMode
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlaybackControlView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * Created by yoshida_makoto on 2016/10/25.
 */
class Player(val context: Context) : ExoPlayer.EventListener,
        TrackSelector.EventListener<MappingTrackSelector.MappedTrackInfo>,
        PlaybackControlView.VisibilityListener {

    private val TAG = "Player"

    val errorObservable = PublishSubject.create<String>()!!
    val maxProgress = BehaviorSubject.create<Int>()!!
    var isPlaying = BehaviorSubject.create<Boolean>()!!
    val durationString = PublishSubject.create<String>()!!
    val playEndSubject = PublishSubject.create<PlayMode.PlayMode>()!!
    val playPreviousSubject = PublishSubject.create<Unit>()!!
    lateinit var currentAudioResource: ExtractorMediaSource

    val playMode = PlayMode()
    val mainHandler = Handler()
    val defaultBandwidthMeter = DefaultBandwidthMeter()
    val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(defaultBandwidthMeter)
    val trackSelector = DefaultTrackSelector(mainHandler, videoTrackSelectionFactory)
    val loadControl: LoadControl = DefaultLoadControl()
    val exoPlayer: SimpleExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
    }

    var playingMusicId: Long = -1 // 未再生時の初期値

    init {
        Injector.component.inject(this)
        exoPlayer.playWhenReady = true
    }

    fun generatePitchFrequency(key: Double): Float {
        return Math.pow(2.0, key / 12).toFloat()
    }

    fun startMusic(music: Music) {
        currentAudioResource = exoPlayer.createAudioSource(context, music.id)
        // Prepare the player with the source.
        // play to play music
        exoPlayer.addListener(this)
        exoPlayer.prepare(currentAudioResource)
        playingMusicId = music.id
        updatePlayState(music.key.toDouble())
    }

    fun updatePlayState(key: Double) {
        val playbackParams = PlaybackParams().apply {
            pitch = generatePitchFrequency(key)
            speed = 1.0f
        }
        exoPlayer.playbackParams = playbackParams
    }

    fun updatePlayState(key: Int) {
        updatePlayState(key.toDouble())
    }

    private fun getDuration(): Int {
        return exoPlayer.duration.toInt()
    }

    fun getDurationString(): String {
        return Util.convertMusicDuration2String(exoPlayer.duration)
    }

    fun getCurrentDurationString(): String {
        return Util.convertMusicDuration2String(exoPlayer.currentPosition)
    }

    fun getCurrentPosition(): Long {
        return exoPlayer.currentPosition
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        errorObservable.onNext("エラー！")
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_READY -> {
                isPlaying.onNext(exoPlayer.playWhenReady)
                durationString.onNext(getDurationString())
                maxProgress.onNext(getDuration())
            }

            ExoPlayer.STATE_ENDED -> {
                exoPlayer.stop()
                playEndSubject.onNext(playMode.currentPlayMode.value)
            }
        }
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.d("デバッグ onLoadingChanged", "isLoading ${isLoading}")
    }

    override fun onPositionDiscontinuity() {
        Log.d("デバッグ onPositionDis...", "よくわからん")
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
        Log.d("デバッグ onTimelineChanged", "timeline ${timeline.toString()}, manifest ${manifest.toString()}")
    }

    fun seekTo(progress: Int) {
        // 時間経過周りの表現はcurrentPosition使えばいけそう　現在の再生秒数的なのを返してくれるっぽい。
        Log.d("デバッグ seekTo", "currentPosition ${exoPlayer.currentPosition}")
        exoPlayer.seekTo(progress.toLong())
    }

    override fun onTrackSelectionsChanged(trackSelections: TrackSelections<out MappingTrackSelector.MappedTrackInfo>?) {
        val info = trackSelections!!.info
        for (rendererIndex in 0..trackSelections.length) {
            val trackGroups = info.getTrackGroups(rendererIndex)
            if (trackGroups.length > 0) {
                Log.d(TAG, "  Renderer:$rendererIndex [")
                for (groupIndex in 0..trackGroups.length - 1) {
                    val trackGroup = trackGroups.get(groupIndex)
                    for (trackIndex in 0..trackGroup.length - 1) {
                        Log.d(TAG, "      " + " Track:" + trackIndex + ", "
                                + getFormatString(trackGroup.getFormat(trackIndex)))
                    }
                    Log.d(TAG, "    ]")
                }
                Log.d(TAG, "  ]")
            }
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    override fun onVisibilityChange(visibility: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getFormatString(format: Format?): String {
        if (format == null) {
            return "null"
        }
        val builder = StringBuilder()
        builder.append("id=").append(format.id).append(", mimeType=").append(format.sampleMimeType)
        if (format.bitrate != Format.NO_VALUE) {
            builder.append(", bitrate=").append(format.bitrate)
        }
        if (format.width != Format.NO_VALUE && format.height != Format.NO_VALUE) {
            builder.append(", res=").append(format.width).append("x").append(format.height)
        }
        if (format.frameRate != Format.NO_VALUE.toFloat()) {
            builder.append(", fps=").append(format.frameRate)
        }
        if (format.channelCount != Format.NO_VALUE) {
            builder.append(", channels=").append(format.channelCount)
        }
        if (format.sampleRate != Format.NO_VALUE) {
            builder.append(", sample_rate=").append(format.sampleRate)
        }
        if (format.language != null) {
            builder.append(", language=").append(format.language)
        }
        return builder.toString()
    }

    fun pause() {
        exoPlayer.playWhenReady = false
    }

    fun play() {
        exoPlayer.playWhenReady = true
    }

    fun setNextPlayMode() {
        playMode.switchNextMode()
    }

    fun goToFinalDuration() {
        exoPlayer.seekTo(exoPlayer.duration)
    }

    fun goToHeadOrPrevious() {
        // 3.5秒が閾値
        if (exoPlayer.currentPosition > 3500) {
            exoPlayer.seekTo(0)
        } else {
            playPreviousSubject.onNext(Unit)
        }
    }
}