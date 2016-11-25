package com.example.yoshida_makoto.kotlintest.entity

import android.databinding.ObservableField
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * RealmObjectを継承したクラスでvalは使えないぽい？あと、open classじゃないとダメっぽい
 * Created by yoshida_makoto on 2016/09/21.
 */
//TODO 曲ごとにメモが残せてもいいかも
open class Music(
        @PrimaryKey var id: Long = 0,
        var title: String = "",
        var artist: String = "",
        key: Int,
        @Ignore var observableKey: ObservableField<Int> = ObservableField(0)) : RealmObject() {

    constructor() : this(0, "", "", 0, ObservableField()) {
    }

    var key: Int = 0
        get() = field
        set(value) {
            field = value
            observableKey.set(value)
        }

    init {
        this.key = key
    }

    fun isContainsString(query: String): Boolean {
        return title.contains(query) || artist.contains(query)
    }
}