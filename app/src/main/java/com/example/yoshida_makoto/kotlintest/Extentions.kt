package com.example.yoshida_makoto.kotlintest

import android.databinding.BindingAdapter
import android.databinding.ObservableArrayList
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.entity.UserPlayList
import com.example.yoshida_makoto.kotlintest.ui.adapter.MusicListAdapter
import com.example.yoshida_makoto.kotlintest.ui.decoration.DividerItemDecoration
import com.example.yoshida_makoto.kotlintest.value.PlayModeValue

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
@BindingAdapter("musicList")
fun RecyclerView.setMusicList(musics: ObservableArrayList<Music>) {
    adapter = MusicListAdapter(this.context, musics)
    musics.addOnListChangedCallback(ObservableListCallback(adapter))
}

@BindingAdapter("playList")
fun RecyclerView.setPlayList(playLists: ObservableArrayList<UserPlayList>) {

}

@BindingAdapter("dividerFor")
fun RecyclerView.setDividerFor(param: String) {
    when (param) {
        "horizontal" -> {
            this.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.HORIZONTAL_LIST))
        }
        else -> {
            this.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL_LIST))
        }
    }
}

@BindingAdapter("keyText")
fun TextView.setKeyText(key: Int) {
    val keyStatus: String
    if (key == 0) {
        keyStatus = "default"
    } else {
        val prefix = if (key > 0) "#" else "♭"
        keyStatus = "$prefix${Math.abs(key)}"
    }
    this.text = "key: ${keyStatus}"
}

@BindingAdapter("isContentsPlaying")
fun ImageView.setIsContentsPlaying(isPlaying: Boolean) {
    if (isPlaying == true) {
        this.setImageResource(R.drawable.ic_pause_white_36dp)
    } else {
        this.setImageResource(R.drawable.ic_play_arrow_white_36dp)
    }
}

@BindingAdapter("playMode")
fun ImageView.setPlayMode(playMode: PlayModeValue.PlayMode) {
    when (playMode) {
        PlayModeValue.PlayMode.DEFAULT -> {
            this.setImageResource(R.drawable.ic_repeat_off_36dp)
        }
        PlayModeValue.PlayMode.REPEAT_ALL -> {
            this.setImageResource(R.drawable.ic_repeat_white_36dp)
        }
        PlayModeValue.PlayMode.REPEAT_ONE -> {
            this.setImageResource(R.drawable.ic_repeat_one_white_36dp)
        }
    }
}

@BindingAdapter("shuffle")
fun ImageView.setShuffle(isShuffle: Boolean) {
    if (isShuffle == true) {
        this.setImageResource(R.drawable.ic_shuffle_white_36dp)
    } else {
        this.setImageResource(R.drawable.ic_shuffle_off_36dp)
    }
}
