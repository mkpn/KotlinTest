package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.view.View
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.entity.UserPlayList
import io.reactivex.subjects.PublishSubject


/**
 * Created by yoshida_makoto on 2016/11/14.
 */
class UserPlayListRowViewModel(val playList: UserPlayList) {
    var playListClickObservable = PublishSubject.create<Long>()

    val clickListener = View.OnClickListener {
        playListClickObservable.onNext(playList.id)
    }
}