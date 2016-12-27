package com.example.yoshida_makoto.kotlintest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.yoshida_makoto.kotlintest.ControllableNotificationService.Command.*
import com.example.yoshida_makoto.kotlintest.di.Injector
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/12/27.
 */

class ControllableNotificationService : Service() {
    @Inject
    lateinit var player: Player

    enum class Command(val command: String) {
        PLAY_NEXT("play_next"),
        PLAY_PREVIOUS("play_previous"),
        PLAY_PAUSE("play_pause")
    }

    override fun onBind(intent: Intent): IBinder? {
        Injector.component.inject(this)
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            PLAY_NEXT.command -> {

            }
            PLAY_PREVIOUS.command -> {

            }
            PLAY_PAUSE.command -> {

            }
            else -> {

            }
        }
        return START_STICKY
    }
}
