package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.databinding.ObservableField
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.domain.SearchMusicsByStringUseCase
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
class MusicListFragmentViewModel {
    val searchMusicsByStringQuery = SearchMusicsByStringUseCase()
    val musics = searchMusicsByStringQuery.searchMusicsByString("") // 初期化
    val disposables = CompositeDisposable()

    val panelState = ObservableField<SlidingUpPanelLayout.PanelState>(PanelState.HIDDEN)
    @Inject
    lateinit var player: Player

    init {
        Injector.component.inject(this)
        disposables.add(player.music.subscribe { music ->
            panelState.set(PanelState.COLLAPSED)
        })
    }

    fun searchMusicByStringQuery(query: String) {
        searchMusicsByStringQuery.searchMusicsByString(query)
    }

    fun clear() {
        disposables.clear()
    }
}
