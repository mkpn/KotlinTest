package com.example.yoshida_makoto.kotlintest.viewmodel

import android.view.View
import com.example.yoshida_makoto.kotlintest.events.TransitToDetailEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by yoshida_makoto on 2016/09/07.
 */
class ItemRowViewModel(val text: String) {
    var clickListener: View.OnClickListener = View.OnClickListener {
        EventBus.getDefault().post(TransitToDetailEvent())
    }
}