package com.example.yoshida_makoto.kotlintest

import android.content.ContentUris
import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.ObservableArrayList
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.ui.adapter.MusicListAdapter
import com.example.yoshida_makoto.kotlintest.ui.decoration.DividerItemDecoration
import com.example.yoshida_makoto.kotlintest.value.PlayMode
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
@BindingAdapter("musicList")
fun RecyclerView.setMusicList(musics: ObservableArrayList<Music>) {
    adapter = MusicListAdapter(this.context, musics)
    musics.addOnListChangedCallback(ObservableListCallback(adapter))
}

@BindingAdapter("dividerFor")
fun RecyclerView.setDividerFor(orientation: Int) {
    this.addItemDecoration(DividerItemDecoration(this.context, orientation))
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
fun ImageView.setPlayMode(playMode: PlayMode.PlayMode) {
    when (playMode) {
        PlayMode.PlayMode.DEFAULT -> {
            this.setImageResource(R.drawable.ic_repeat_off_36dp)
        }
        PlayMode.PlayMode.REPEAT_ALL -> {
            this.setImageResource(R.drawable.ic_repeat_white_36dp)
        }
        PlayMode.PlayMode.REPEAT_ONE -> {
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

fun ExoPlayer.createAudioSource(context: Context, musicId: Long): ExtractorMediaSource {
    val defaultBandwidthMeter = DefaultBandwidthMeter()
    val dataSourceFactory = DefaultDataSourceFactory(context,
            Util.getUserAgent(context, context.getString(R.string.app_name)), defaultBandwidthMeter)
    // Produces Extractor instances for parsing the media data.
    val extractorsFactory = DefaultExtractorsFactory()
    val trackUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicId)
    // This is the MediaSource representing the media to be played.
    return ExtractorMediaSource(trackUri, dataSourceFactory, extractorsFactory, null, null)
}
