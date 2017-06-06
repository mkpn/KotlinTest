package com.example.yoshida_makoto.kotlintest

import android.app.Application
import com.example.yoshida_makoto.kotlintest.di.AppComponent
import com.example.yoshida_makoto.kotlintest.di.DaggerAppComponent
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.di.RootModule
import io.realm.Realm
import timber.log.Timber

/**
 * Created by yoshida_makoto on 2016/10/26.
 */
class MyApplication : Application() {
    val applicationComponent: AppComponent by lazy { DaggerAppComponent.builder().rootModule(RootModule(this)).build() }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Timber.plant(Timber.DebugTree())
        Injector.init(applicationComponent)
    }
}
