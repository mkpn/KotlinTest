package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.databinding.ObservableField
import android.support.v7.widget.SearchView
import com.example.yoshida_makoto.kotlintest.MyError
import com.example.yoshida_makoto.kotlintest.MySuccess
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.query.SearchMusicsByStringQuery
import com.example.yoshida_makoto.kotlintest.ui.decoration.DividerItemDecoration
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
// TODO permissionCheckerもらわないとだめかも
class MainViewModel() {
    val musicsCommand = MusicsCommand()
    val searchMusicsByStringQuery = SearchMusicsByStringQuery()
    val musics = searchMusicsByStringQuery.searchMusicsByString("") // 初期化
    val disposables = CompositeDisposable()

    val successStream = BehaviorSubject.create<MySuccess>()
    val errorStream = BehaviorSubject.create<MyError>()
    val recyclerViewOrientation = DividerItemDecoration.VERTICAL_LIST
    val panelState = ObservableField<SlidingUpPanelLayout.PanelState>(PanelState.HIDDEN)
    @Inject
    lateinit var player: Player

    init {
        Injector.component.inject(this)
        musicsCommand.successStream.subscribe { successStream.onNext(MySuccess()) }
        musicsCommand.errorStream.subscribe { errorStream.onNext(MyError()) }
        disposables.add(player.music.subscribe{ music ->
            panelState.set(PanelState.COLLAPSED)
        })
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
