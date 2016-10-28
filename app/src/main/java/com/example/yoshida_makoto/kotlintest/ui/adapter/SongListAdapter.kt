package com.example.yoshida_makoto.kotlintest.ui.adapter

import android.content.Context
import android.databinding.ObservableArrayList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.yoshida_makoto.kotlintest.databinding.ItemRowBinding
import com.example.yoshida_makoto.kotlintest.entity.Song

/**
 * Created by yoshida_makoto on 2016/09/06.
 */
class SongListAdapter(val context: Context, val songList: ObservableArrayList<Song>) : MyListAdapter<Song>(songList) {
    private val inflater: LayoutInflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val binding = (holder as ContentViewHolder).binding
        val song = dataList.get(position)
        binding.song = song
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return ContentViewHolder(ItemRowBinding.inflate(inflater, parent, false))
    }

    private class ContentViewHolder(var binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.getRoot())
}