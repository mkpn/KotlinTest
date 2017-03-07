package com.example.yoshida_makoto.kotlintest

import android.content.ContentUris
import android.content.Context
import android.media.PlaybackParams
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.domain.FindNextMusicUseCase
import com.example.yoshida_makoto.kotlintest.domain.FindNextMusicWithLoopUseCase
import com.example.yoshida_makoto.kotlintest.domain.FindPreviousMusicUseCase
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.value.PlayMode
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlaybackControlView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Created by yoshida_makoto on 2016/10/25.
 */
class Player(val context: Context) : ExoPlayer.EventListener,
        TrackSelector.EventListener<MappingTrackSelector.MappedTrackInfo>,
        PlaybackControlView.VisibilityListener, PlayerInterface {

    private val TAG = "Player"
    val disposables = CompositeDisposable()
    val keyChangeDisposables = CompositeDisposable()
    val timeObservable = io.reactivex.Observable.interval(1, TimeUnit.SECONDS, Schedulers.newThread())!!

    val findNextMusicQuery = FindNextMusicUseCase()
    val findNextMusicWithLoopQuery = FindNextMusicWithLoopUseCase()
    val findPreviousMusicUseCase = FindPreviousMusicUseCase()

    val musicsCommand = MusicsCommand()
    val music = BehaviorSubject.create<Music>()
    val errorObservable = PublishSubject.create<String>()!!
    val maxProgress = BehaviorSubject.create<Int>()!!
    var isPlaying = BehaviorSubject.create<Boolean>()!!
    val durationString = PublishSubject.create<String>()!!
    val currentDurationSubject = PublishSubject.create<String>()!!
    val currentProgressValueSubject = PublishSubject.create<Int>()!!
    val keyChangeSubject = PublishSubject.create<Int>()!!
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
        isPlaying.onNext(false)
        disposables.addAll(
                findNextMusicQuery.musicSubject.subscribe { targetMusic ->
                    initializeMusic(targetMusic)
                    playMusic()
                },
                findNextMusicQuery.allMusicPlayFinishSubject.subscribe { unit ->
                    //                    stop()
                    pause()
                },
                findNextMusicWithLoopQuery.musicSubject.subscribe { targetMusic ->
                    initializeMusic(targetMusic)
                    playMusic()
                },
                findPreviousMusicUseCase.musicSubject.subscribe { targetMusic ->
                    initializeMusic(targetMusic)
                    playMusic()
                },
                findPreviousMusicUseCase.musicSubject.subscribe { targetMusic ->
                    initializeMusic(targetMusic)
                    playMusic()
                }
        )
        exoPlayer.addListener(this)
    }

    override fun initializeMusic(music: Music) {
        stop()
        this.music.onNext(music)
        updatePlayState(music.key.toDouble())
        keyChangeDisposables.clear()
        keyChangeDisposables.add(
                music.keySubject.subscribe { key ->
                    updatePlayState(key.toDouble())
                    keyChangeSubject.onNext(key)
                    musicsCommand.updateOrCreateMusic(music.id)
                }
        )

        currentAudioResource = createAudioSource(context, music.id)
        playingMusicId = music.id
        exoPlayer.prepare(currentAudioResource)
    }

    override fun pause() {
        isPlaying.onNext(false)
        exoPlayer.playWhenReady = false
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun skipToNext() {
        Log.d("デバッグ", "skipToNext")
        when (playMode.currentPlayMode.value) {
            PlayMode.PlayMode.REPEAT_ALL -> {
                findNextMusicWithLoopQuery.find(music.value)
            }
            else -> {
                findNextMusicQuery.find(music.value)
            }
        }
    }

    override fun goToHeadOrPrevious() {
        // 3.5秒を閾値として、曲の最初に戻るか、前の曲を再生するかの処理に分かれる
        if (exoPlayer.currentPosition > 3500) {
            exoPlayer.seekTo(0)
        } else {
            findPreviousMusicUseCase.find(music.value)
        }
    }

    override fun playNextWithOutLoop() {
        findNextMusicQuery.find(music.value)
    }

    override fun playNextWithLoop() {
        findNextMusicWithLoopQuery.find(music.value)
    }

    fun generatePitchFrequency(key: Double): Float {
        return Math.pow(2.0, key / 12).toFloat()
    }

    fun playMusic() {
        isPlaying.onNext(true)
        exoPlayer.playWhenReady = true
    }

    fun playMusic(music: Music) {
        initializeMusic(music)
        playMusic()
    }

    fun updatePlayState(key: Double) {
        val playbackParams = PlaybackParams().apply {
            pitch = generatePitchFrequency(key)
            speed = 1.0f
        }
        exoPlayer.playbackParams = playbackParams
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
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_READY -> {
                isPlaying.onNext(exoPlayer.playWhenReady)
                durationString.onNext(getDurationString())
                maxProgress.onNext(getDuration())

                timeObservable.filter { isPlaying.value }
                        .subscribe { second ->
                            currentDurationSubject.onNext(getCurrentDurationString())
                            currentProgressValueSubject.onNext(getCurrentPosition().toInt())
                        }
            }

            ExoPlayer.STATE_ENDED -> {
                seekTo(0)
                currentProgressValueSubject.onNext(getCurrentPosition().toInt())
                currentDurationSubject.onNext(getCurrentDurationString())

                when (playMode.currentPlayMode.value) {
                    PlayMode.PlayMode.REPEAT_ALL -> {
                        playNextWithLoop()
                    }
                    PlayMode.PlayMode.REPEAT_ONE -> {
                    }
                    else -> {
                        playNextWithOutLoop()
                    }
                }
            }
        }
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.d("デバッグ onLoadingChanged", "isLoading ${isLoading}")
    }

    override fun onPositionDiscontinuity() {
        Log.d("デバッグ onPositionDis...", "シークバーなどで再生箇所がジャンプした時")
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
        Log.d("デバッグ onTimelineChanged", "timeline ${timeline.toString()}, manifest ${manifest.toString()}")
    }

    fun seekTo(progress: Int) {
        // 時間経過周りの表現はcurrentPosition使えばいけそう　現在の再生秒数的なのを返してくれるっぽい。
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

    fun switchPlayMode() {
        playMode.switchPlayMode()
    }

    fun changePitch(changeValue: Int) {
        music.value.changeKey(changeValue)
    }

    fun playOrPause() {
        when (isPlaying.value) {
            true -> {
                pause()
            }
            false -> {
                playMusic()
            }
        }
    }

    fun createAudioSource(context: Context, musicId: Long): ExtractorMediaSource {
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(context,
                com.google.android.exoplayer2.util.Util.getUserAgent(context, context.getString(R.string.app_name)), defaultBandwidthMeter)
        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory()
        val trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicId)
        // This is the MediaSource representing the media to be played.
        return ExtractorMediaSource(trackUri, dataSourceFactory, extractorsFactory, null, null)
    }
}