package com.example.yoshida_makoto.kotlintest

import java.util.concurrent.TimeUnit

/**
 * Created by yoshida_makoto on 2016/12/09.
 */
class Util {
    companion object {
        fun convertMusicDuration2String(duration: Long): String {
            val s = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            val m = TimeUnit.MILLISECONDS.toMinutes(duration)

            return "%02d:%02d".format(m, s)
        }
    }
}