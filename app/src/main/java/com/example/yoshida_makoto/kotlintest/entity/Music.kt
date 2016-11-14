package com.example.yoshida_makoto.kotlintest.entity

import android.view.View
import com.example.yoshida_makoto.kotlintest.events.ClickMusicEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by yoshida_makoto on 2016/09/21.
 */
//TODO 曲ごとにメモが残せてもいいかも
class Music(val id: Long, val title: String, val artist: String) {

    fun isContains(query: String): Boolean {
        return title.contains(query) || artist.contains(query)
    }

    var musicClickListener: View.OnClickListener = View.OnClickListener {
        EventBus.getDefault().post(ClickMusicEvent(id))

    }
}