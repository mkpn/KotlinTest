package com.example.yoshida_makoto.kotlintest.di

import com.example.yoshida_makoto.kotlintest.NotificationPlayerPanelService
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.query.*
import com.example.yoshida_makoto.kotlintest.ui.activity.MainActivity
import com.example.yoshida_makoto.kotlintest.ui.adapter.MusicListAdapter
import com.example.yoshida_makoto.kotlintest.ui.fragment.MusicListFragment
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MainViewModel
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MusicListFragmentViewModel
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.PlayerViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Created by yoshida_makoto on 2016/10/26.
 */
@Component(modules = arrayOf(RootModule::class, ClientModule::class))
@Singleton
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(vm: MainViewModel)
    fun inject(player: Player)
    fun inject(adapter: MusicListAdapter)
    fun inject(command: MusicsCommand)
    fun inject(query: FindMusicByIdQuery)
    fun inject(query: SearchMusicsByStringQuery)
    fun inject(query: FindNextMusicQuery)
    fun inject(query: FindPreviousMusicQuery)
    fun inject(playerViewModel: PlayerViewModel)
    fun inject(findNextMusicWithLoopQuery: FindNextMusicWithLoopQuery)
    fun inject(updatePlayListQuery: UpdatePlayListQuery)
    fun inject(sortPlayListQuery: SortPlayListQuery)
    fun inject(notificationPlayerPanelService: NotificationPlayerPanelService)
    fun inject(musicListFragmentViewModel: MusicListFragmentViewModel)
    fun inject(musicListFragment: MusicListFragment)
}