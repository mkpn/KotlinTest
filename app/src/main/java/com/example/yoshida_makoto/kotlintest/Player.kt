package com.example.yoshida_makoto.kotlintest

import android.content.ContentUris
import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import android.media.PlaybackParams
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlaybackControlView
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import rx.subjects.PublishSubject

/**
 * Created by yoshida_makoto on 2016/10/25.
 */
class Player(val context: Context) : ExoPlayer.EventListener,
        TrackSelector.EventListener<MappingTrackSelector.MappedTrackInfo>,
        PlaybackControlView.VisibilityListener {

    private val TAG = "Player"

    val errorObservable: PublishSubject<String> = PublishSubject.create()
    val mainHandler = Handler()
    val defaultBandwidthMeter = DefaultBandwidthMeter()
    val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(defaultBandwidthMeter)
    val trackSelector = DefaultTrackSelector(mainHandler, videoTrackSelectionFactory)
    val loadControl: LoadControl = DefaultLoadControl()
    val exoPlayer: SimpleExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
    }
    var key: ObservableField<Float> = ObservableField(0.0f)

    init {
        exoPlayer.playWhenReady = true

        key.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                Log.d("デバッグ", "onProperty Changed!!!!!")
            }
        })
    }

    fun sendChangePitchMessage(keyDifference: Int) {
        // ここでキー変えてる まじ大事なところ
        key.set(key.get() + keyDifference)
        val pitchFreq = generatePitchFrequency(key.get())
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

        // 曲変えても、前の曲でいじったkeyの値を引き回してしまう。。。
        // TODO SongEntityにkeyの情報もたせてそれに合わせて再生するようにすれば良さげ
        key.set(0.0f)
        key.get()
        val playbackParams = PlaybackParams().apply {
            // TODO ここでDBの設定とか読み込んで反映できたらいいなー
            pitch = 1.0f
            speed = 1.0f
        }
        exoPlayer.playbackParams = playbackParams
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