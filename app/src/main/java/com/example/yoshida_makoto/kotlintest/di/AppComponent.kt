package com.example.yoshida_makoto.kotlintest.di

import com.example.yoshida_makoto.kotlintest.NotificationPlayerPanelService
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.Receiver
import com.example.yoshida_makoto.kotlintest.command.MusicsCommand
import com.example.yoshida_makoto.kotlintest.domain.*
import com.example.yoshida_makoto.kotlintest.repository.MusicsRepository
import com.example.yoshida_makoto.kotlintest.ui.activity.MainActivity
import com.example.yoshida_makoto.kotlintest.ui.adapter.UserPlayListAdapter
import com.example.yoshida_makoto.kotlintest.ui.fragment.MusicListFragment
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MainViewModel
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MusicListFragmentViewModel
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MusicRowViewModel
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
    fun inject(query: SearchMusicsByStringUseCase)
    fun inject(query: FindPreviousMusicUseCase)
    fun inject(playerViewModel: PlayerViewModel)
    fun inject(findNextMusicWithLoopQuery: FindNextMusicWithLoopUseCase)
    fun inject(notificationPlayerPanelService: NotificationPlayerPanelService)
    fun inject(musicListFragmentViewModel: MusicListFragmentViewModel)
    fun inject(musicListFragment: MusicListFragment)
    fun inject(userPlayListAdapter: UserPlayListAdapter)
    fun inject(musicsRepository: MusicsRepository)
    fun inject(playMusicUseCase: PlayMusicUseCase)
    fun inject(musicsCommand: MusicsCommand)
    fun inject(pauseMusicUseCase: PlayOrPauseMusicUseCase)
    fun inject(musicKeyChangeUseCase: MusicKeyChangeUseCase)
    fun inject(skipMusicUseCase: SkipMusicUseCase)
    fun inject(goToHeadOrPreviousUseCase: GoToHeadOrPreviousUseCase)
    fun inject(switchPlayModeUseCase: SwitchPlayModeUseCase)
    fun inject(shufflePlayListUseCase: SortPlayListUseCase)
    fun inject(findNextMusicUseCase: FindNextMusicUseCase)
    fun inject(musicRowViewModel: MusicRowViewModel)
    fun inject(receiver: Receiver)
}