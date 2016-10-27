package com.example.yoshida_makoto.kotlintest

import android.app.Application

import com.example.yoshida_makoto.kotlintest.dagger.AppComponent
import com.example.yoshida_makoto.kotlintest.dagger.DaggerAppComponent
import com.example.yoshida_makoto.kotlintest.dagger.RootModule

/**
 * Created by yoshida_makoto on 2016/10/26.
 */

class MyApplication : Application() {
    val applicationComponent: AppComponent by lazy { DaggerAppComponent.builder().rootModule(RootModule(this)).build() }
}
