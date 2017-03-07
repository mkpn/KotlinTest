package com.example.yoshida_makoto.kotlintest.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.databinding.MusicListFragmentBinding
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.ui.decoration.DividerItemDecoration
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MusicListFragmentViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by yoshida_makoto on 2016/12/16.
 */
class MusicListFragment : Fragment() {
    lateinit var binding: MusicListFragmentBinding
    val vm: MusicListFragmentViewModel = MusicListFragmentViewModel()
    val disposables = CompositeDisposable()

    companion object {
        fun newInstance(): MusicListFragment {
            return MusicListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.music_list_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MusicListFragmentBinding.bind(view)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.vm = vm
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.clear()
    }

    fun searchMusicByStringQuery(query: String) {
        vm.searchMusicByStringQuery(query)
    }
}
