package com.example.yoshida_makoto.kotlintest.di

import android.content.Context
import com.example.yoshida_makoto.kotlintest.MyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by yoshida_makoto on 2016/10/26.
 */
@Module
class RootModule(val application: MyApplication){
    @Provides
    @Singleton
    fun provideApplicaitonContext(): Context = application.applicationContext
}