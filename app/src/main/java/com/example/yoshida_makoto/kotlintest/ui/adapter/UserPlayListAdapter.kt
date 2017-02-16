package com.example.yoshida_makoto.kotlintest.ui.adapter

import android.content.Context
import android.databinding.ObservableArrayList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.yoshida_makoto.kotlintest.Messenger
import com.example.yoshida_makoto.kotlintest.MyApplication
import com.example.yoshida_makoto.kotlintest.databinding.MusicRowBinding
import com.example.yoshida_makoto.kotlintest.databinding.UserPlayListRowBinding
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.entity.UserPlayList
import com.example.yoshida_makoto.kotlintest.messages.ClickMusicMessage
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MusicRowViewModel
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.UserPlayListRowViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/09/06.
 */
class UserPlayListAdapter(val context: Context, playLists: ObservableArrayList<UserPlayList>) : MyListAdapter<UserPlayList>(playLists) {
    init {
        (context.applicationContext as MyApplication).applicationComponent.inject(this)
    }

    val desposables = CompositeDisposable()

    private val inflater: LayoutInflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }

    lateinit private var vm: UserPlayListRowViewModel

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val binding = (holder as ContentViewHolder).binding
        val music = dataList.get(position)
        vm = UserPlayListRowViewModel(music)
        binding.vm = vm
        desposables.add(vm.playListClickObservable.subscribe { playListId ->
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return ContentViewHolder(UserPlayListRowBinding.inflate(inflater, parent, false))
    }

    private class ContentViewHolder(var binding: UserPlayListRowBinding) : RecyclerView.ViewHolder(binding.getRoot())
}