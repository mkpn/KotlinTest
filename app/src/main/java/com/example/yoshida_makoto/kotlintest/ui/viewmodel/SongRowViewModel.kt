package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.view.View
import com.example.yoshida_makoto.kotlintest.entity.Song
import com.example.yoshida_makoto.kotlintest.events.ClickSongEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by yoshida_makoto on 2016/09/07.
 */
class SongRowViewModel(val song: Song) {
    var songClickListener: View.OnClickListener = View.OnClickListener {
        EventBus.getDefault().post(ClickSongEvent(song.id))
    }

    fun getSongIdString(): String {
        return song.id.toString()
    }
}