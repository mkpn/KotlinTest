package com.example.yoshida_makoto.kotlintest.query

import com.example.yoshida_makoto.kotlintest.dagger.Injector
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.repository.MusicsRepository
import javax.inject.Inject

/**
 * Musicに対して、副作用のない命令(readだけになるかも)をするクラス
 * Created by yoshida_makoto on 2016/11/18.
 */
class FindPreviousMusicQuery() {
    init {
        Injector.component.inject(this)
    }

    @Inject
    lateinit var musicsRepository: MusicsRepository

    val musicSubject = musicsRepository.musicSubject

    fun find(music: Music) {
        musicsRepository.findPreviousMusicFromPlayList(music)
    }
}
