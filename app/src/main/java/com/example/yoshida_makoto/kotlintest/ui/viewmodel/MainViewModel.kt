package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.support.v7.widget.SearchView
import com.example.yoshida_makoto.kotlintest.MyError
import com.example.yoshida_makoto.kotlintest.MySuccess
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.query.SearchMusicsByStringQuery
import com.example.yoshida_makoto.kotlintest.ui.decoration.DividerItemDecoration
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
// TODO permissionCheckerもらわないとだめかも
class MainViewModel() {
    val musicsCommand = MusicsCommand()
    val searchMusicsByStringQuery = SearchMusicsByStringQuery()
    val musics = searchMusicsByStringQuery.searchMusicsByString("") // 初期化

    val successStream = BehaviorSubject.create<MySuccess>()
    val errorStream = BehaviorSubject.create<MyError>()
    val recyclerViewOrientation = DividerItemDecoration.VERTICAL_LIST

    init {
        musicsCommand.successStream.subscribe { successStream.onNext(MySuccess()) }
        musicsCommand.errorStream.subscribe { errorStream.onNext(MyError()) }
    }

    val textChangeListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(query: String): Boolean {
            searchMusicsByStringQuery.searchMusicsByString(query)
            return false
        }
    }
}
