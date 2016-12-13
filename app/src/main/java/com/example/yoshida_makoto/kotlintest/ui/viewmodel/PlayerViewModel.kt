package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.databinding.Observable
import android.databinding.ObservableField
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.example.yoshida_makoto.kotlintest.MySuccess
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.Util
import com.example.yoshida_makoto.kotlintest.dagger.Injector
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/18.
 */

class PlayerViewModel(musicId: Long) {
    @Inject
    lateinit var player: Player
    val disposables = CompositeDisposable()
    val playerLaunchDisposables = CompositeDisposable()
    val launchSubject = BehaviorSubject.create<MySuccess>()
    val timeObservable = io.reactivex.Observable.interval(1, TimeUnit.SECONDS, Schedulers.newThread())!!

    lateinit var timerDisposable: Disposable

    val durationString = ObservableField<String>("00:00")
    val currentTimeString = ObservableField<String>("00:00")
    val maxProgressValue = ObservableField(0)
    val currentProgressValue = ObservableField(0)
    var isSeekBarMovable = true

    init {
        Injector.component.inject(this)
        disposables.add(player.musicLoadedSubject.subscribe {
            player.music.observableKey.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    player.updatePlayState()
                }
            })
        })

        disposables.add(player.musicDurationFixedSubject.subscribe {
            durationString.set(player.getDurationString())
            maxProgressValue.set(player.getDuration().toInt())
            timerDisposable = timeObservable.subscribe { second ->
                currentTimeString.set(player.getCurrentDurationString())
                currentProgressValue.set(player.getCurrentPosition().toInt())
            }
        })

        playerLaunchDisposables.add(player.launchPlayerSubject.subscribe {
            launchSubject.onNext(MySuccess())
            playerLaunchDisposables.dispose()
        })

        player.launchByMusicId(musicId)
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
        player.changePitch(1)
    }

    val pitchDownClickListener = View.OnClickListener {
        player.changePitch(-1)
    }

    val playButtonClickListener = View.OnClickListener {
        timerDisposable.dispose()
    }

}