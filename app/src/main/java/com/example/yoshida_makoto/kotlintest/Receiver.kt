package com.example.yoshida_makoto.kotlintest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.yoshida_makoto.kotlintest.di.Injector
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2017/06/06.
 */
class Receiver : BroadcastReceiver() {
    @Inject
    lateinit var player: Player

    init {
        Injector.component.inject(this)
    }

    // TODO 後で参考　プラットフォームによって音楽再生のステータスが変わると飛んでくるintent違う？
    // https://github.com/android/platform_packages_apps_music/blob/master/src/com/android/music/MediaPlaybackService.java
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("デバッグ onReceive ${intent?.action}")
        val action = intent?.action ?: return
        when (action) {
            "com.android.music.playstatechanged" -> {
                Timber.d("デバッグ ACTION_AUDIO_BECOMING_NOISY")
                player.pause()
            }
        }
    }

}