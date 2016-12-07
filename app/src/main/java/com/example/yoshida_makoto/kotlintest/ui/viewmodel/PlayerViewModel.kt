package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.databinding.Observable
import android.databinding.ObservableField
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.dagger.Injector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/18.
 */

class PlayerViewModel(musicId: Long) {
    @Inject
    lateinit var player: Player
    val disposables = CompositeDisposable()

    val durationString = ObservableField<String>("00:00")
    val currentTimeString = ObservableField<String>("00:00")

    init {
        Injector.component.inject(this)
        disposables.add(player.musicLoadedStream.subscribe {
            player.music.observableKey.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    player.playCurrentMusic()
                }
            })
        })

        disposables.add(player.musicDurationFixedStream.subscribe {
            durationString.set(player.getDurationString())
        })

        player.playMusic(musicId)
    }

    val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            Log.d("デバッグ", "onProgressChanged!! ${progress}")
            player.seekTo(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {

        }
    }

    val pitchUpClickListener = View.OnClickListener {
        player.changePitch(1)
    }

    val pitchDownClickListener = View.OnClickListener {
        player.changePitch(-1)
    }

}