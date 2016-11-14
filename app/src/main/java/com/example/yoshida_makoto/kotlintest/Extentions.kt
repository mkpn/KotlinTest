package com.example.yoshida_makoto.kotlintest

import android.databinding.BindingAdapter
import android.databinding.ObservableArrayList
import android.support.v7.widget.RecyclerView
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.ui.adapter.MusicListAdapter
import com.example.yoshida_makoto.kotlintest.ui.decoration.DividerItemDecoration

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
@BindingAdapter("musicList")
fun RecyclerView.setMusicList(musics: ObservableArrayList<Music>) {
    adapter = MusicListAdapter(this.context, musics)
    musics.addOnListChangedCallback(ObservableListCallback(adapter))
    this.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL_LIST))
}