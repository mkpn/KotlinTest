package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.Util
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.domain.*
import com.example.yoshida_makoto.kotlintest.value.PlayModeValue
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/18.
 */
class PlayerViewModel {
    @Inject
    lateinit var player: Player

    val disposables = CompositeDisposable()

    val musicTitle = ObservableField<String>("")
    val artistName = ObservableField<String>("")
    val durationString = ObservableField<String>("00:00")
    val currentTimeString = ObservableField<String>("00:00")
    var currentMusicKey = ObservableField(0)
    val playMode = ObservableField<PlayModeValue.PlayMode>()
    val maxProgressValue = ObservableField(0)
    val currentProgressValue = ObservableField(0)
    val isShuffle = ObservableBoolean(false)
    val contentsIsPlaying = ObservableBoolean(true)
    var isSeekBarMovable = true
    val playOrPauseUseCase = PlayOrPauseMusicUseCase()
    val keyChangeUseCase = MusicKeyChangeUseCase()
    val skipMusicUsecase = SkipMusicUseCase()
    val goToHeadOrPreviousUseCase = GoToHeadOrPreviousUseCase()
    val switchPlayModeUseCase = SwitchPlayModeUseCase()
    val sortPlayListUseCase = SortPlayListUseCase()

    init {
        Injector.component.inject(this)

        disposables.addAll(
                player.music.subscribe { music ->
                    musicTitle.set(music.title)
                    artistName.set(music.artist)
                    currentMusicKey.set(music.key)
                },
                player.keyChangeSubject.subscribe { key ->
                    currentMusicKey.set(key)
                },
                player.playMode.currentPlayMode.subscribe { playMode ->
                    this.playMode.set(playMode)
                },
                player.maxProgress.subscribe { progress ->
                    maxProgressValue.set(progress)
                },
                player.durationString.subscribe { string ->
                    durationString.set(string)
                },
                player.currentProgressValueSubject.filter { isSeekBarMovable }
                        .subscribe { value ->
                            currentProgressValue.set(value)
                        },
                player.currentDurationSubject.filter { isSeekBarMovable }
                        .subscribe { string ->
                            currentTimeString.set(string)
                        },
                player.isPlaying.subscribe { isPlaying ->
                    contentsIsPlaying.set(isPlaying)
                    contentsIsPlaying.notifyChange() //なんでbooleanだけこれ呼ばなきゃならんの、、、
                }
        )
    }

    val seekBarTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_BUTTON_PRESS -> {
                isSeekBarMovable = false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> {
                isSeekBarMovable = true
            }
        }
        false
    }

    val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            currentTimeString.set(Util.convertMusicDuration2String(progress.toLong()))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            player.seekTo(seekBar.progress)
        }
    }

    val pitchUpClickListener = View.OnClickListener {
        keyChangeUseCase.changeKey(1)
    }

    val pitchDownClickListener = View.OnClickListener {
        keyChangeUseCase.changeKey(-1)
    }

    val playButtonClickListener = View.OnClickListener {
        playOrPauseUseCase.playOrPause()
    }

    val playNextButtonClickListener = View.OnClickListener {
        skipMusicUsecase.skip()
    }

    val playPreviousButtonClickListener = View.OnClickListener {
        goToHeadOrPreviousUseCase.goToHeadOrPrevious()
    }

    val playModeIconClickListener = View.OnClickListener {
        switchPlayModeUseCase.switch()
    }

    val shuffleIconClickListner = View.OnClickListener {
        isShuffle.set(!isShuffle.get())
        sortPlayListUseCase.sort(isShuffle.get())
        isShuffle.notifyChange()
    }
}