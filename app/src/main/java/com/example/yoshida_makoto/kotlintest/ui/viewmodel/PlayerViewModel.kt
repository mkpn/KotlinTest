package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.Util
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.query.*
import com.example.yoshida_makoto.kotlintest.value.PlayMode
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/18.
 */
class PlayerViewModel() {
    @Inject
    lateinit var player: Player

    // 画面が回転したりするとnullになるっぽい
    lateinit var music: Music

    val findNextMusicQuery = FindNextMusicQuery()
    val findNextMusicWithLoopQuery = FindNextMusicWithLoopQuery()
    val findMusicByIdQuery = FindMusicByIdQuery()
    val findPreviousMusicQuery = FindPreviousMusicQuery()
    val updatePlayListQuery = UpdatePlayListQuery()
    val sortPlayListQuery = SortPlayListQuery()
    val disposables = CompositeDisposable()
    val musicsCommand = MusicsCommand()
    val timeObservable = io.reactivex.Observable.interval(1, TimeUnit.SECONDS, Schedulers.newThread())!!

    val musicTitle = ObservableField<String>("")
    val artistName = ObservableField<String>("")
    val durationString = ObservableField<String>("00:00")
    val currentTimeString = ObservableField<String>("00:00")
    var currentMusicKey = ObservableField(0)
    val playMode = ObservableField<PlayMode.PlayMode>()
    val maxProgressValue = ObservableField(0)
    val currentProgressValue = ObservableField(0)
    val isShuffle = ObservableBoolean(false)
    val contentsIsPlaying = ObservableBoolean(true)
    var isSeekBarMovable = true

    init {
        Injector.component.inject(this)

        disposables.addAll(
                player.playMode.currentPlayMode.subscribe { playMode ->
                    this.playMode.set(playMode)
                },
                player.maxProgress.subscribe { progress ->
                    maxProgressValue.set(progress)
                },
                player.durationString.subscribe { string ->
                    durationString.set(string)
                },
                timeObservable.filter { contentsIsPlaying.get() && isSeekBarMovable }
                        .subscribe { second ->
                            currentTimeString.set(player.getCurrentDurationString())
                            currentProgressValue.set(player.getCurrentPosition().toInt())
                        },
                player.isPlaying.subscribe { isPlaying ->
                    contentsIsPlaying.set(isPlaying)
                    contentsIsPlaying.notifyChange() //なんでbooleanだけこれ呼ばなきゃならんの、、、
                },
                player.playEndSubject.subscribe { playMode ->
                    when (playMode) {
                        PlayMode.PlayMode.REPEAT_ALL -> {
                            findNextMusicWithLoopQuery.find(music)
                        }
                        PlayMode.PlayMode.REPEAT_ONE -> {
                            playMusic(music)
                        }
                        else -> {
                            findNextMusicQuery.find(music)
                        }
                    }
                },
                findNextMusicQuery.musicSubject.subscribe { targetMusic ->
                    playMusic(targetMusic)
                },
                findMusicByIdQuery.musicSubject
                        .filter { targetMusic ->
                            !contentsIsPlaying.get() || player.playingMusicId != targetMusic.id
                        }
                        .subscribe { targetMusic ->
                            playMusic(targetMusic)
                        },
                findPreviousMusicQuery.musicSubject.subscribe { targetMusic ->
                    playMusic(targetMusic)
                },
                player.playPreviousSubject.subscribe { success ->
                    findPreviousMusicQuery.find(music)
                }
        )
    }

    // 音楽リストのタップ動作で再生を開始する
    fun startMusicByTap(musicId: Long) {
        updatePlayListQuery.update()
        findMusicByIdQuery.findMusic(musicId)
    }

    private fun playMusic(targetMusic: Music) {
        music = targetMusic
        musicTitle.set(targetMusic.title)
        artistName.set(targetMusic.artist)
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
        if (contentsIsPlaying.get() == true) {
            player.pause()
        } else {
            player.play()
        }
    }

    val playNextButtonClickListener = View.OnClickListener {
        player.goToFinalDuration()
    }

    val playPreviousButtonClickListener = View.OnClickListener {
        player.goToHeadOrPrevious()
    }

    val playModeIconClickListener = View.OnClickListener {
        player.setNextPlayMode()
    }

    val shuffleIconClickListner = View.OnClickListener {
        isShuffle.set(!isShuffle.get())
        sortPlayListQuery.sort(isShuffle.get())
        isShuffle.notifyChange()
    }
}