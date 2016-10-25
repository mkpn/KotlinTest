package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.databinding.ObservableField

/**
 * Created by yoshida_makoto on 2016/09/01.
 */
class ScheduleViewModel(time: String, task: String) {
    var time: ObservableField<String> = ObservableField()
    var task: ObservableField<String> = ObservableField()

    init {
        this.time.set(time)
        this.task.set(task)
    }
}
