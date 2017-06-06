package com.example.yoshida_makoto.kotlintest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Created by yoshida_makoto on 2017/06/06.
 */
class Receiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Timber.d("デバッグ onReceive")
    }

}