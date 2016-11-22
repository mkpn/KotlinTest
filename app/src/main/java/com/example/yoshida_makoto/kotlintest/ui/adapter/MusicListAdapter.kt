package com.example.yoshida_makoto.kotlintest.ui.adapter

import android.content.Context
import android.databinding.ObservableArrayList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.yoshida_makoto.kotlintest.Messenger
import com.example.yoshida_makoto.kotlintest.MyApplication
import com.example.yoshida_makoto.kotlintest.databinding.MusicRowBinding
import com.example.yoshida_makoto.kotlintest.entity.Music
import com.example.yoshida_makoto.kotlintest.messages.ClickMusicMessage
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MusicRowViewModel
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by yoshida_makoto on 2016/09/06.
 */
class MusicListAdapter(val context: Context, musicList: ObservableArrayList<Music>) : MyListAdapter<Music>(musicList) {
    init {
        (context.applicationContext as MyApplication).applicationComponent.inject(this)
    }

    val subscriptions = CompositeSubscription()

    @Inject
    lateinit var messenger: Messenger

    private val inflater: LayoutInflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }

    lateinit private var vm: MusicRowViewModel

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val binding = (holder as ContentViewHolder).binding
        val music = dataList.get(position)
        vm = MusicRowViewModel(music)
        binding.vm = vm
        subscriptions.add(vm.musicClickObservable.subscribe { musicId ->
            messenger.send(ClickMusicMessage(musicId)) })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return ContentViewHolder(MusicRowBinding.inflate(inflater, parent, false))
    }

    private class ContentViewHolder(var binding: MusicRowBinding) : RecyclerView.ViewHolder(binding.getRoot())
}