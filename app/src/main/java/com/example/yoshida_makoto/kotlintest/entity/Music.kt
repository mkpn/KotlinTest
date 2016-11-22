package com.example.yoshida_makoto.kotlintest.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * RealmObjectを継承したクラスでvalは使えないぽい？open classじゃないとダメっぽい感じもある
 * Created by yoshida_makoto on 2016/09/21.
 */
//TODO 曲ごとにメモが残せてもいいかも
open class Music(
        @PrimaryKey var id: Long = 0,
        var title: String = "",
        var artist: String = "",
        var pitch: Long = 0) : RealmObject() {
    fun isContainsString(query: String): Boolean {
        return title.contains(query) || artist.contains(query)
    }
}