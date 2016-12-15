package com.example.yoshida_makoto.kotlintest.entity

import io.reactivex.subjects.BehaviorSubject
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.util.regex.Pattern

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
        @Ignore val keySubject: BehaviorSubject<Int> = BehaviorSubject.create<Int>()) : RealmObject() {

    constructor() : this(0, "", "", 0, BehaviorSubject.create<Int>()) {
    }

    var key: Int = 0
        get() = field
        set(value) {
            field = value
            keySubject.onNext(value)
        }

    init {
        this.key = key
    }

    fun isContainsString(query: String): Boolean {
        if (query.isEmpty()) return true

        //大文字小文字を区別しない
        val p = Pattern.compile(query, Pattern.CASE_INSENSITIVE)
        val titleMatcher = p.matcher(title)
        val artistMatcher = p.matcher(artist)
        return titleMatcher.find() || artistMatcher.find()
    }
}