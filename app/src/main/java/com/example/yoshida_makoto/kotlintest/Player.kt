package com.example.yoshida_makoto.kotlintest

import android.content.Context
import android.media.PlaybackParams
import android.os.Handler
import android.util.Log
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.dagger.Injector
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.query.FindMusicByIdQuery
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlaybackControlView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * Created by yoshida_makoto on 2016/10/25.
 */
class Player(val context: Context) : ExoPlayer.EventListener,
        TrackSelector.EventListener<MappingTrackSelector.MappedTrackInfo>,
        PlaybackControlView.VisibilityListener {

    private val TAG = "Player"

    val musicsCommand = MusicsCommand()
    val findMusicByIdQuery = FindMusicByIdQuery()
    val musicLoadedStream = BehaviorSubject.create<Music>()
    val musicDurationFixedStream = BehaviorSubject.create<String>()
    val errorObservable = PublishSubject.create<String>()
    val mainHandler = Handler()
    val defaultBandwidthMeter = DefaultBandwidthMeter()
    val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(defaultBandwidthMeter)
    val trackSelector = DefaultTrackSelector(mainHandler, videoTrackSelectionFactory)
    val loadControl: LoadControl = DefaultLoadControl()
    val exoPlayer: SimpleExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
    }
    lateinit var music: Music

    init {
        Injector.component.inject(this)
        exoPlayer.playWhenReady = true
    }

    fun generatePitchFrequency(key: Double): Float {
        return Math.pow(2.0, key / 12).toFloat()
    }

    fun playMusic(musicId: Long) {
        Single.create<Music> { emitter ->
            this.music = findMusicByIdQuery.findMusic(musicId)!!
            val audioSource = exoPlayer.createAudioSource(context, musicId)
            // Prepare the player with the source.
            // start to play music
            exoPlayer.prepare(audioSource)
            updatePlayState()
            exoPlayer.addListener(this)
            emitter.onSuccess(music)
        }.subscribe { music ->
            musicLoadedStream.onNext(music)
        }
    }

    fun updatePlayState() {
        val playbackParams = PlaybackParams().apply {
            pitch = generatePitchFrequency(music.observableKey.get().toDouble())
            speed = 1.0f
        }
        exoPlayer.playbackParams = playbackParams
    }

    fun changePitch(i: Int) {
        musicsCommand.updateOrCreatePitch(music.id, i)
    }

    fun getDurationString(): String {
        return Util.convertMusicDuration2String(exoPlayer.duration)
    }

    fun getCurrentDurationString(): String{
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
        if (playbackState == ExoPlayer.STATE_READY) {
            Single.create<String> { emitter ->
                emitter.onSuccess(getDurationString())
            }.subscribe { durationString ->
                musicDurationFixedStream.onNext(durationString)
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

    fun getDuration(): Long {
        return exoPlayer.duration
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
}