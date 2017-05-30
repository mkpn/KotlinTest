package com.example.yoshida_makoto.kotlintest.domain

import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.repository.MusicsRepository
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2017/02/16.
 */
class SortPlayListUseCase {
    init {
        Injector.component.inject(this)
    }

    @Inject
    lateinit var repository: MusicsRepository

    fun sort(isShuffle: Boolean) {
        repository.sortPlayList(isShuffle)
    }
}