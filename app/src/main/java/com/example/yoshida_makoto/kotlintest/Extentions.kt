package com.example.yoshida_makoto.kotlintest

import android.content.ContentUris
import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.ObservableArrayList
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.ui.adapter.MusicListAdapter
import com.example.yoshida_makoto.kotlintest.ui.decoration.DividerItemDecoration
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.util.concurrent.TimeUnit

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

fun ExoPlayer.getDurationString(): String {
    val s = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
    val m = TimeUnit.MILLISECONDS.toMinutes(duration)
    Log.d("デバッグ onLoadingChanged", "s is ${s}")
    Log.d("デバッグ onLoadingChanged", "m is ${m}")

    return "%02d:%02d".format(m, s)
}