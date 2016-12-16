package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.databinding.ObservableField
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.Util
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.dagger.Injector
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.query.FindMusicByIdQuery
import com.example.yoshida_makoto.kotlintest.query.FindNextMusicQuery
import com.example.yoshida_makoto.kotlintest.query.FindPreviousMusicQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/18.
 */

class PlayerViewModel(musicId: Long) {
    @Inject
    lateinit var player: Player
    lateinit var music: Music

    val findNextMusicQuery = FindNextMusicQuery()
    val findMusicByIdQuery = FindMusicByIdQuery()
    val findPreviousMusicQuery = FindPreviousMusicQuery()
    val disposables = CompositeDisposable()
    val musicsCommand = MusicsCommand()
    val timeObservable = io.reactivex.Observable.interval(1, TimeUnit.SECONDS, Schedulers.newThread())!!

    val durationString = ObservableField<String>("00:00")
    val currentTimeString = ObservableField<String>("00:00")
    var currentMusicKey = ObservableField(0)
    val maxProgressValue = ObservableField(0)
    val currentProgressValue = ObservableField(0)
    val isPlaying = ObservableField<Boolean>(true)
    var isSeekBarMovable = true

    init {
        Injector.component.inject(this)

        disposables.addAll(
                player.maxProgress.subscribe { progress ->
                    maxProgressValue.set(progress)
                },
                player.durationString.subscribe { string ->
                    durationString.set(string)
                },
                timeObservable.filter { isPlaying.get() && isSeekBarMovable }
                        .subscribe { second ->
                            currentTimeString.set(player.getCurrentDurationString())
                            currentProgressValue.set(player.getCurrentPosition().toInt())
                        },
                player.isPlaying.subscribe { isPlaying ->
                    Log.d("デバッグ", "is playin subscribe!!!")
                    this.isPlaying.set(isPlaying)
                },
                player.playEndSubject.subscribe { success ->
                    findNextMusicQuery.find(music)
                },
                findNextMusicQuery.musicSubject.subscribe { targetMusic ->
                    playMusic(targetMusic)
                },
                findMusicByIdQuery.musicSubject
                        .filter { targetMusic ->
                            !isPlaying.get() || player.playingMusicId != targetMusic.id
                        }
                        .subscribe { targetMusic ->
                            playMusic(targetMusic)
                        },
                findPreviousMusicQuery.musicSubject.subscribe { targetMusic ->
                    playMusic(targetMusic)
                }
        )
        findMusicByIdQuery.findMusic(musicId)
    }

    private fun playMusic(targetMusic: Music) {
        music = targetMusic
        player.startMusic(music)
        disposables.add(
                music.keySubject.subscribe { key ->
                    currentMusicKey.set(key)
                    player.updatePlayState(key)
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
        musicsCommand.updateOrCreatePitch(music.id, 1)
    }

    val pitchDownClickListener = View.OnClickListener {
        musicsCommand.updateOrCreatePitch(music.id, -1)
    }

    val playButtonClickListener = View.OnClickListener {
        Log.d("デバッグ", "playButtonClickListener")
        when (isPlaying.get()) {
            true -> {
                player.pause()
            }
            false -> {
                player.play()
            }
        }
    }

    val playNextButtonClickListener = View.OnClickListener {
        findNextMusicQuery.find(music)
    }

    val playPreviousButtonClickListener = View.OnClickListener {
        findPreviousMusicQuery.find(music)
    }

}