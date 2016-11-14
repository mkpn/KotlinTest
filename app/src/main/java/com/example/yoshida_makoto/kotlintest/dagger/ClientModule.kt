package com.example.yoshida_makoto.kotlintest.dagger

import android.content.ContentResolver
import android.content.Context
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.repository.MusicRepository
import dagger.Module
import dagger.Provides
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
    fun provideMusicRepository(contentResolver: ContentResolver): MusicRepository = MusicRepository(contentResolver)
}