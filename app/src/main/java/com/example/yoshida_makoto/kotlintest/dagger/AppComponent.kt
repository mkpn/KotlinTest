package com.example.yoshida_makoto.kotlintest.dagger

import com.example.yoshida_makoto.kotlintest.repository.MusicRepository
import com.example.yoshida_makoto.kotlintest.ui.activity.MainActivity
import com.example.yoshida_makoto.kotlintest.ui.activity.PlayerActivity
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MainViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Created by yoshida_makoto on 2016/10/26.
 */
@Component(modules = arrayOf(RootModule::class, ClientModule::class))
@Singleton
interface AppComponent {
    fun inject(playerActivity: PlayerActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(musicRepository: MusicRepository)
    fun inject(vm: MainViewModel)
}