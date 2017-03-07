package com.example.yoshida_makoto.kotlintest.value

import io.reactivex.subjects.BehaviorSubject

/**
 * Created by yoshida_makoto on 2016/12/22.
 */
class PlayMode {
    enum class PlayMode {
        DEFAULT,
        REPEAT_ALL,
        REPEAT_ONE
    }

    val playModeList = arrayOf(PlayMode.DEFAULT, PlayMode.REPEAT_ALL, PlayMode.REPEAT_ONE)
    var currentPlayMode = BehaviorSubject.create<PlayMode>()

    init {
        currentPlayMode.onNext(playModeList[0])
    }

    fun switchPlayMode() {
        val nextIndex: Int
        if (playModeList.indexOf(currentPlayMode.value) >= playModeList.size - 1) {
            nextIndex = 0
        } else {
            nextIndex = playModeList.indexOf(currentPlayMode.value) + 1
        }
        currentPlayMode.onNext(playModeList.get(nextIndex))
    }
}
