package com.example.yoshida_makoto.kotlintest.repository

import android.content.ContentResolver
import android.databinding.ObservableArrayList
import android.provider.MediaStore
import com.example.yoshida_makoto.kotlintest.entity.Music
import rx.Emitter
import rx.Observable

/**
 * Created by yoshida_makoto on 2016/11/11.
 */
class MusicRepository(val contentResolver: ContentResolver) {

    val musics: ObservableArrayList<Music> = ObservableArrayList()

    fun clearMusic() {
        musics.clear()
    }

    fun findSongListObservable(query: String) {
        Observable.fromEmitter<Music>(
                { emitter ->
                    val musicResolver = contentResolver
                    val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    val musicCursor = musicResolver.query(musicUri, null, null, null, null)
                    if (musicCursor != null && musicCursor.moveToFirst()) {
                        val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                        val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                        val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                        do {
                            val thisId = musicCursor.getLong(idColumn)
                            val thisTitle = musicCursor.getString(titleColumn)
                            val thisArtist: String = musicCursor.getString(artistColumn) ?: "no data"
                            val music = Music(thisId, thisTitle, thisArtist)
                            if (query.isEmpty() || music.isContains(query)) emitter.onNext(music)
                        } while (musicCursor.moveToNext())
                        musicCursor.close()
                    }
                    emitter.onCompleted()
                }, Emitter.BackpressureMode.LATEST)
                .subscribe { music -> musics.add(music) }
    }
}