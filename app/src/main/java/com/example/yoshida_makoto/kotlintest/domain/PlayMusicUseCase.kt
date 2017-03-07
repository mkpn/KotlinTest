package com.example.yoshida_makoto.kotlintest.domain

import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.repository.MusicsRepository
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2017/02/16.
 */
class PlayMusicUseCase {
    init {
        Injector.component.inject(this)
    }

    @Inject
    lateinit var repository: MusicsRepository
    @Inject
    lateinit var player: com.example.yoshida_makoto.kotlintest.Player

    fun play(musicId: Long) {
        repository.updatePlayList()
        repository.findMusicSubject.subscribe { targetMusic ->
            player.playMusic(targetMusic)
        }
        repository.findMusicById(musicId)
    }
}