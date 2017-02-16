package com.example.yoshida_makoto.kotlintest.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.databinding.UserPlayListFragmentBinding
import com.example.yoshida_makoto.kotlintest.ui.AddPlayListMenuDialog
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.UserPlayListFragmentViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by yoshida_makoto on 2016/12/16.
 */
class UserPlayListFragment : Fragment() {
    lateinit var binding: UserPlayListFragmentBinding
    val vm: UserPlayListFragmentViewModel = UserPlayListFragmentViewModel()
    val disposables = CompositeDisposable()

    companion object {
        fun newInstance(): UserPlayListFragment {
            return UserPlayListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposables.addAll(
                vm.showAddPlayListMenuSubject.subscribe{unit ->
                    AddPlayListMenuDialog(activity).show()
                }
        )
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.user_play_list_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UserPlayListFragmentBinding.bind(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.vm = vm
    }

    override fun onDestroy() {
        super.onDestroy()
//        vm.disposables.dispose()
    }

}
