package com.example.yoshida_makoto.kotlintest.domain

import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.di.Injector
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2017/02/16.
 */
class MusicKeyChangeUseCase {
    init {
        Injector.component.inject(this)
    }

    @Inject
    lateinit var player: Player

    /**
     * value: キーの変更量
     */
    fun changeKey(value: Int){
        player.changePitch(value)
    }
}