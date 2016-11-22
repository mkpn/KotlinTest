package com.example.yoshida_makoto.kotlintest.dagger

/**
 * Created by yoshida_makoto on 2016/11/22.
 */

object Injector {
    lateinit var component: AppComponent

    fun init(appComponent: AppComponent) {
        component = appComponent
    }
}