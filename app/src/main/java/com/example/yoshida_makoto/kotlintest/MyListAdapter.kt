package com.example.yoshida_makoto.kotlintest

import android.databinding.ObservableArrayList
import android.support.v7.widget.RecyclerView

/**
 * Created by yoshida_makoto on 2016/09/06.
 */
abstract class MyListAdapter<T>(protected var dataList: ObservableArrayList<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun isEmpty(): Boolean {
        return dataList.isEmpty()
    }
}