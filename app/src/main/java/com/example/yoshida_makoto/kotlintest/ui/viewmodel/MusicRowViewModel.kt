package com.example.yoshida_makoto.kotlintest.ui.viewmodel

import android.view.View
import com.example.yoshida_makoto.kotlintest.Messenger
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.messages.ClickMusicMessage

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
class MusicRowViewModel(val music: Music){
    val messenger = Messenger()
    val clickListener = View.OnClickListener {
        messenger.send(ClickMusicMessage(music.id))
    }
}