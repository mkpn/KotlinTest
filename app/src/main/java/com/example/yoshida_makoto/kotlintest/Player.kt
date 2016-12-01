package com.example.yoshida_makoto.kotlintest

import android.content.Context
import android.databinding.Observable
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/10/25.
 */
class Player(val context: Context) : ExoPlayer.EventListener,
        TrackSelector.EventListener<MappingTrackSelector.MappedTrackInfo>,
        PlaybackControlView.VisibilityListener {

    private val TAG = "Player"
    @Inject
    lateinit var messenger: Messenger
    val musicsCommand = MusicsCommand()
    val findMusicByIdQuery = FindMusicByIdQuery()
    val musicLoadedStream = BehaviorSubject.create<Music>()

    val errorObservable: PublishSubject<String> = PublishSubject.create()
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
        io.reactivex.Observable.create<Music> { emitter ->
            this.music = findMusicByIdQuery.findMusic(musicId)!!
            music.observableKey.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    val playbackParams = PlaybackParams().apply {
                        pitch = generatePitchFrequency(music.observableKey.get().toDouble())
                        speed = 1.0f
                    }
                    exoPlayer.playbackParams = playbackParams
                }
            })

            val audioSource = exoPlayer.createAudioSource(context, musicId)
            // Prepare the player with the source.
            exoPlayer.prepare(audioSource)

            val playbackParams = PlaybackParams().apply {
                pitch = generatePitchFrequency(music.observableKey.get().toDouble())
                speed = 1.0f
            }
            exoPlayer.playbackParams = playbackParams
            emitter.onNext(music)
        }.subscribe { music -> musicLoadedStream.onNext(music) }
    }

    fun changePitch(i: Int) {
        musicsCommand.updateOrCreatePitch(music.id, i)
    }

    fun setPlayerView(playerView: SimpleExoPlayerView?) {
        playerView!!.player = exoPlayer
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        errorObservable.onNext("エラー！")
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
}