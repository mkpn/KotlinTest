package com.example.yoshida_makoto.kotlintest.dagger

import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.query.FindMusicByIdQuery
import com.example.yoshida_makoto.kotlintest.query.SearchMusicsByStringQuery
import com.example.yoshida_makoto.kotlintest.ui.activity.MainActivity
import com.example.yoshida_makoto.kotlintest.ui.activity.PlayerActivity
import com.example.yoshida_makoto.kotlintest.ui.adapter.MusicListAdapter
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
    fun inject(vm: MainViewModel)
    fun inject(player: Player)
    fun inject(adapter: MusicListAdapter)
    fun inject(command: MusicsCommand)
    fun inject(query: FindMusicByIdQuery)
    fun inject(query: SearchMusicsByStringQuery)
}