package com.example.yoshida_makoto.kotlintest.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.dagger.Injector
import com.example.yoshida_makoto.kotlintest.databinding.PlayerFragmentBinding
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.PlayerViewModel

/**
 * Created by yoshida_makoto on 2016/12/16.
 */
class PlayerFragment : Fragment() {
    lateinit var binding: PlayerFragmentBinding
    lateinit var vm: PlayerViewModel

    companion object{
        fun newInstance(): PlayerFragment{
            return PlayerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.player_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PlayerFragmentBinding.bind(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Injector.component.inject(this)
        vm = PlayerViewModel()
        // vm.PlayMusicCommandみたいにするのが良さげ
        binding.vm = vm
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.disposables.dispose()
    }

    fun startMusic(musicId: Long){
        vm.startMusic(musicId)
    }
}
