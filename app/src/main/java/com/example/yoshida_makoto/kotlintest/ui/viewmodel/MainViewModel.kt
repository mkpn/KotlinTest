package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.support.v7.widget.SearchView
import com.example.yoshida_makoto.kotlintest.Messenger
import com.example.yoshida_makoto.kotlintest.dagger.AppComponent
import com.example.yoshida_makoto.kotlintest.repository.MusicRepository
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
// TODO permissionCheckerもらわないとだめかも
class MainViewModel (applicationComponent: AppComponent){
    init {
        applicationComponent.inject(this)
        // 音楽リストの初期化
        musicRepository.findSongListObservable("")
    }

    val messenger = Messenger()
    @Inject
    lateinit var musicRepository: MusicRepository

    val musics = musicRepository.musics

    val textChangeListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(query: String?): Boolean {
            musicRepository.clearMusic()
            musicRepository.findSongListObservable(query ?: "")
            return false
        }
    }
}
