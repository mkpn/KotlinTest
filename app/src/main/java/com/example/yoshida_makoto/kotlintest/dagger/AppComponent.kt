package com.example.yoshida_makoto.kotlintest.dagger

import com.example.yoshida_makoto.kotlintest.ui.activity.PlayerActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by yoshida_makoto on 2016/10/26.
 */
@Component(modules = arrayOf(RootModule::class, ClientModule::class))
@Singleton
interface AppComponent {
    fun inject(playerActivity: PlayerActivity)
}