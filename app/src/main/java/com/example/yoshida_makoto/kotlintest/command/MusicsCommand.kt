package com.example.yoshida_makoto.kotlintest.command

import com.example.yoshida_makoto.kotlintest.MyError
import com.example.yoshida_makoto.kotlintest.MySuccess
import com.example.yoshida_makoto.kotlintest.dagger.Injector
import com.example.yoshida_makoto.kotlintest.repository.MusicsRepository
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Musicsに対する、作成・更新の命令をするクラス
 * Created by yoshida_makoto on 2016/11/18.
 */
class MusicsCommand() {
    init {
        Injector.component.inject(this)
    }

    @Inject
    lateinit var musicsRepository: MusicsRepository

    val successStream = BehaviorSubject.create<MySuccess>()
    val errorStream = BehaviorSubject.create<MyError>()

    init {
        musicsRepository.successStream.subscribe { successStream.onNext(MySuccess()) }
        musicsRepository.successStream.subscribe { errorStream.onNext(MyError()) }
    }

    fun updateOrCreatePitch(musicId: Long, pitch: Long) {
        musicsRepository.updateOrCreatePitch(musicId, pitch)
    }

    fun searchMusic(query: String) {
        musicsRepository.findSongListObservable(query)
    }

    fun findAllMusic() {
        musicsRepository.findSongListObservable("")
    }
}
