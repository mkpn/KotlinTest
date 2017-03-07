package com.example.yoshida_makoto.kotlintest.repository

import android.content.ContentResolver
import android.databinding.ObservableArrayList
import android.provider.MediaStore
import android.util.Log
import com.example.yoshida_makoto.kotlintest.MyError
import com.example.yoshida_makoto.kotlintest.MySuccess
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.entity.Music
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.realm.Realm
import java.util.*
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/11/11.
 */
class MusicsRepository(val contentResolver: ContentResolver) {

    @Inject
    lateinit var realm: Realm

    // 端末内の音楽保持しておく
    val masterMusics: ObservableArrayList<Music> = ObservableArrayList()

    // ページ表示用の音楽リスト
    val targetMusics: ObservableArrayList<Music> = ObservableArrayList()
    // 実際に再生するための音楽リスト
    var currentPlayList: ArrayList<Music> = ArrayList()

    val successStream = BehaviorSubject.create<MySuccess>()
    val errorStream = BehaviorSubject.create<MyError>()

    val findMusicSubject = PublishSubject.create<Music>()
    val nextMusicSubject = PublishSubject.create<Music>()
    val previousMusicSubject = PublishSubject.create<Music>()
    val allMusicPlayFinishSubject = PublishSubject.create<Unit>()

    init {
        Injector.component.inject(this)

        // 初期化時、全楽曲を取得する
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
                    val thisArtist: String = musicCursor.getString(artistColumn) ?: "no artist data"

                    val savedMusic: Music? = realm.where(Music::class.java).equalTo("id", thisId).findFirst()
                    val music = Music(thisId, thisTitle, thisArtist, savedMusic?.key ?: 0)

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
                .subscribe { musics ->
                    Log.d("デバッグ", "masterMusics.size after initialize is ${masterMusics.size}")
                    successStream.onNext(MySuccess()) }
    }

    // keyの変更とか、musicに対してuserが何か更新した時はrealmに保存する
    fun updateOrCreateMusic(musicId: Long) {
        Observable.fromIterable(masterMusics)
                .filter { music -> music.id.equals(musicId) }
                .subscribe { music ->
                    realm.beginTransaction()
                    realm.copyToRealmOrUpdate(music)
                    realm.commitTransaction()
                }
    }

    fun searchMusicsByString(query: String): ObservableArrayList<Music> {
        targetMusics.clear()
        masterMusics.forEach { music ->
            if (music.isContainsString(query)) targetMusics.add(music)
        }
        targetMusics.sortBy { music -> music.title }
        Log.d("デバッグ", "targetMusics.size after initialize is ${targetMusics.size}")
        return targetMusics
    }

    fun sortPlayList(isShuffle: Boolean) {
        if (isShuffle) {
            Collections.shuffle(currentPlayList)
        } else {
            currentPlayList.sortBy { music -> music.title }
        }
    }

    // 表示中の音楽リストを"タップして"再生が始まる時は、
    // これから再生されるプレイリストを表示中の音楽リストに上書きする
    fun updatePlayList() {
        currentPlayList = targetMusics
    }

    fun findMusicById(musicId: Long) {
        masterMusics.forEach { music ->
            if (music.id == musicId) {
                findMusicSubject.onNext(music)
                return
            }
        }
    }

    fun findNextMusicFromPlayList(music: Music) {
        val targetIndex: Int
        if (music != currentPlayList.last()) {
            targetIndex = currentPlayList.indexOf(music) + 1
            val nextMusic = currentPlayList.get(targetIndex)
            nextMusicSubject.onNext(nextMusic)
        } else {
            allMusicPlayFinishSubject.onNext(Unit)
        }
    }

    fun findPreviousMusicFromPlayList(music: Music) {
        when (currentPlayList.indexOf(music)) {
            0 -> {
                previousMusicSubject.onNext(music)
            }
            else -> {
                val targetIndex: Int
                targetIndex = currentPlayList.indexOf(music) - 1
                val nextMusic = currentPlayList.get(targetIndex)
                previousMusicSubject.onNext(nextMusic)
            }
        }
    }

    fun findNextMusicWithLoopFromPlayList(music: Music) {
        val targetIndex: Int
        when (currentPlayList.indexOf(music)) {
            currentPlayList.size - 1 -> {
                targetIndex = 0
            }
            else -> {
                targetIndex = currentPlayList.indexOf(music) + 1
            }
        }
        Log.d("デバッグ", "currentPlayList size is " + currentPlayList.size)
        Log.d("デバッグ", "target index is " + targetIndex)

        val nextMusic = currentPlayList.get(targetIndex)
        nextMusicSubject.onNext(nextMusic)
    }
}