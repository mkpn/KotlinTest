package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.support.v7.widget.SearchView
import com.example.yoshida_makoto.kotlintest.MyError
import com.example.yoshida_makoto.kotlintest.MySuccess
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.query.MusicsQuery
import rx.subjects.BehaviorSubject

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
// TODO permissionCheckerもらわないとだめかも
class MainViewModel() {
    val musicsQuery = MusicsQuery()
    val musicsCommand = MusicsCommand()
    val musics = musicsQuery.readMusics()

    val successStream = BehaviorSubject.create<MySuccess>()
    val errorStream = BehaviorSubject.create<MyError>()

    init {
        musicsCommand.successStream.subscribe { successStream.onNext(MySuccess()) }
        musicsCommand.errorStream.subscribe { errorStream.onNext(MyError()) }
        musicsCommand.findAllMusic()
    }

    val textChangeListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(query: String): Boolean {
//            musicsRepository.clearMusic()
            musicsCommand.searchMusic(query)
            return false
        }
    }
}
