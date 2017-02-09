package com.example.yoshida_makoto.kotlintest

import com.example.yoshida_makoto.kotlintest.entity.Music

/**
 * Created by yoshida_makoto on 2017/02/07.
 */
interface PlayerInterface {
    fun initializeMusic(music: Music)
    fun pause()
    fun stop()
    fun skipToNext()
    fun goToHeadOrPrevious()
    fun playNextWithOutLoop()
    fun playNextWithLoop()
    fun keyUp()
    fun keyDown()
}