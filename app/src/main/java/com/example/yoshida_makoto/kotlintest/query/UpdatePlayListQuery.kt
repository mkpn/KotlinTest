package com.example.yoshida_makoto.kotlintest.query

import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.repository.MusicsRepository
import javax.inject.Inject

class UpdatePlayListQuery {
    init {
        Injector.component.inject(this)
    }

    @Inject
    lateinit var musicsRepository: MusicsRepository

    fun update() {
        musicsRepository.updatePlayList()
    }
}
