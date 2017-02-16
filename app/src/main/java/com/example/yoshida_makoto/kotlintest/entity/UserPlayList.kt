package com.example.yoshida_makoto.kotlintest.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * RealmObjectを継承したクラスでvalは使えないぽい？あと、open classじゃないとダメっぽい
 */
//TODO 曲ごとにメモが残せてもいいかも
open class UserPlayList(
        @PrimaryKey var id: Long = 0,
        @Required var title: String = "") : RealmObject() {

    constructor() : this(0, "")
}