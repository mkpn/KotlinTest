package com.example.yoshida_makoto.kotlintest

import android.app.Application
import android.content.Intent
import android.util.Log

import com.example.yoshida_makoto.kotlintest.di.AppComponent
import com.example.yoshida_makoto.kotlintest.di.DaggerAppComponent
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.di.RootModule
import io.realm.Realm

/**
 * Created by yoshida_makoto on 2016/10/26.
 */
class MyApplication : Application() {
    val applicationComponent: AppComponent by lazy { DaggerAppComponent.builder().rootModule(RootModule(this)).build() }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Injector.init(applicationComponent)
        Log.d("デバッグ", "startService")
        val intent = Intent(this, NotificationPlayerPanelService::class.java)
        startService(intent)
    }
}
