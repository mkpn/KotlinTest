package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.view.View
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.domain.PlayMusicUseCase
import com.example.yoshida_makoto.kotlintest.entity.Music


/**
 * Created by yoshida_makoto on 2016/11/14.
 */
class MusicRowViewModel(val music: Music) {
    init {
        Injector.component.inject(this)
    }

    val playMusicUseCase = PlayMusicUseCase()

    val clickListener = View.OnClickListener {
        playMusicUseCase.play(music.id)
    }
}