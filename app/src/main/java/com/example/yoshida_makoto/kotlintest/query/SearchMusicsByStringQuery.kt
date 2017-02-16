package com.example.yoshida_makoto.kotlintest.query

import android.databinding.ObservableArrayList
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.repository.MusicsRepository
import javax.inject.Inject

/**
 * Musicに対して、副作用のない命令(readだけになるかも)をするクラス
 * Created by yoshida_makoto on 2016/11/18.
 */
class SearchMusicsByStringQuery {
    init {
        Injector.component.inject(this)
    }

    @Inject
    lateinit var musicsRepository: MusicsRepository

    fun searchMusicsByString(query: String): ObservableArrayList<Music> {
        // TODO 検討：　検索文字列から音楽リストを取得する場合、検索結果はどこに保持するべきか問題。
        return musicsRepository.searchMusicsByString(query)
    }
}
