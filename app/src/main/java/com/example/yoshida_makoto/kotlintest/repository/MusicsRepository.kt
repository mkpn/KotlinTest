package com.example.yoshida_makoto.kotlintest.repository

import android.content.ContentResolver
import android.databinding.ObservableArrayList
import android.provider.MediaStore
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
    val musics: ObservableArrayList<Music> = ObservableArrayList()

    val successStream = BehaviorSubject.create<MySuccess>()
    val errorStream = BehaviorSubject.create<MyError>()

    fun clearMusic() {
        musics.clear()
    }

    fun updateOrCreatePitch(musicId: Long, pitch: Long) {
        Observable.fromIterable(musics)
                .filter { music -> music.id.equals(musicId) }
                .subscribe { music ->
                    music.pitch = pitch
                    realm.copyToRealmOrUpdate(music)
                }
    }

    fun findSongListObservable(query: String) {
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
                    if (query.isEmpty() || music.isContainsString(query)) musics.add(music)
                } while (musicCursor.moveToNext())
                musicCursor.close()
                emitter.onNext(musics)
                emitter.onComplete()
            } else {
                emitter.onError(RuntimeException())
            }
        }
                .doOnError { throwable -> errorStream.onNext(MyError()) }
                .subscribe { musics -> successStream.onNext(MySuccess()) }
    }

    fun findSong(musicId: Long): Music? {
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
                if (musicId == thisId) return music
            } while (musicCursor.moveToNext())
            musicCursor.close()
        }
        return null
    }
}