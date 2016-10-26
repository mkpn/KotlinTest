package com.example.yoshida_makoto.kotlintest.dagger

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by yoshida_makoto on 2016/10/26.
 */
@Module
class RootModule(val application: Application){
    @Provides
    @Singleton
    fun provideApplicaitonContext(): Context = application.applicationContext
}