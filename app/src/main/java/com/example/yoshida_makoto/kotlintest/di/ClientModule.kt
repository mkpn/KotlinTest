package com.example.yoshida_makoto.kotlintest.di

import android.content.ContentResolver
import android.content.Context
import com.example.yoshida_makoto.kotlintest.Messenger
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.repository.MusicsRepository
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Singleton

/**
 * Created by yoshida_makoto on 2016/10/26.
 */
@Module
class ClientModule {
    @Provides
    @Singleton
    fun providePlayer(context: Context): Player = Player(context)

    @Provides
    fun contentResolver(context: Context): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideMusicRepository(contentResolver: ContentResolver): MusicsRepository = MusicsRepository(contentResolver)

    @Provides
    @Singleton
    fun provideMessenger(): Messenger = Messenger()

    @Provides
    fun provideRealm(): Realm {
//        val config = RealmConfiguration
//                .Builder()
//                .schemaVersion(3)
//                .migration(Migration())
//                .build()
        return Realm.getDefaultInstance()
    }
}