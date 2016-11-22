package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.view.View
import rx.subjects.PublishSubject

/**
 * Created by yoshida_makoto on 2016/11/18.
 */

class PlayerViewModel() {
    var pitchChangeObservable = PublishSubject.create<Int>()

    val pitchUpClickListner = View.OnClickListener {
        pitchChangeObservable.onNext(1)
    }

    val pitchDownClickListner = View.OnClickListener {
        pitchChangeObservable.onNext(-1)
    }
}