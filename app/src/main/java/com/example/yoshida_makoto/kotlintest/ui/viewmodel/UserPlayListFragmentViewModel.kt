package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.view.View
import io.reactivex.subjects.PublishSubject

/**
 * Created by yoshida_makoto on 2017/02/15.
 */
class UserPlayListFragmentViewModel {
    val showAddPlayListMenuSubject = PublishSubject.create<Unit>()

    fun getAddButtonClickListener(): View.OnClickListener {
        return View.OnClickListener {
            showAddPlayListMenuSubject.onNext(Unit)
        }
    }
}