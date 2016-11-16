package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.support.v7.widget.SearchView
import com.example.yoshida_makoto.kotlintest.repository.MusicRepository

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
// TODO permissionCheckerもらわないとだめかも
class MainViewModel(musicRepository: MusicRepository) {
    init {
        musicRepository.findSongListObservable("") // 音楽リストの初期化
    }
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
