package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.view.View
import com.example.yoshida_makoto.kotlintest.entity.Music
import io.reactivex.subjects.PublishSubject



/**
 * Created by yoshida_makoto on 2016/11/14.
 */
class MusicRowViewModel(val music: Music){
    var musicClickObservable = PublishSubject.create<Long>()

    val clickListener = View.OnClickListener {
        musicClickObservable.onNext(music.id)
    }
}