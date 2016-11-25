package com.example.yoshida_makoto.kotlintest.repository

import android.content.ContentResolver
import android.databinding.ObservableArrayList
import android.provider.MediaStore
import android.util.Log
import com.example.yoshida_makoto.kotlintest.MyError
import com.example.yoshida_makoto.kotlintest.MySuccess
import com.example.yoshida_makoto.kotlintest.entity.Music
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.realm.Realm

/**
 * Created by yoshida_makoto on 2016/11/11.
 */
class MusicsRepository(val contentResolver: ContentResolver) {

    val realm = Realm.getDefaultInstance()
    val masterMusics: ObservableArrayList<Music> = ObservableArrayList()

    // 音楽一覧ページ表示用
    val targetMusics: ObservableArrayList<Music> = ObservableArrayList()

    val successStream = BehaviorSubject.create<MySuccess>()
    val errorStream = BehaviorSubject.create<MyError>()

    fun clearMusic() {
        masterMusics.clear()
    }

    init {
        initializeMusics() // 初期化時、全楽曲を取得する
    }

    fun updateOrCreatePitch(musicId: Long, key: Int) {
        Observable.fromIterable(masterMusics)
                .filter { music -> music.id.equals(musicId) }
                .subscribe { music ->
                    val a = music.key + key
                    music.key = a
                    realm.beginTransaction()
                    realm.copyToRealmOrUpdate(music)
                    realm.commitTransaction()
                }
    }

    fun initializeMusics() {
        //全音楽を取得し、musicsにaddする
        Observable.create<ObservableArrayList<Music>> { emitter ->
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
                    val music = Music(thisId, thisTitle, thisArtist, 0)
                    masterMusics.add(music)
                } while (musicCursor.moveToNext())
                musicCursor.close()
                emitter.onNext(masterMusics)
                emitter.onComplete()
            } else {
                emitter.onError(RuntimeException())
            }
        }
                .doOnError { throwable -> errorStream.onNext(MyError()) }
                .subscribe { musics -> successStream.onNext(MySuccess()) }
    }

    fun searchMusicsByString(query: String): ObservableArrayList<Music> {
        Log.d("デバッグ", "searchMusicsByString!!! ${query}")
        targetMusics.clear()
        masterMusics.forEach { music -> if (music.isContainsString(query)) targetMusics.add(music) }
        return targetMusics
    }

    fun findSongById(musicId: Long): Music? {
        masterMusics.forEach { music ->
            if (music.id == musicId) return music
        }
        return null
    }
}