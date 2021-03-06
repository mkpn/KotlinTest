package com.example.yoshida_makoto.kotlintest

import android.databinding.ObservableList

/**
 * Created by yoshida_makoto on 2016/10/07.
 */
class ObservableListCallback<T : ObservableList<T>>(private val adapter: ListAdapter) : ObservableList.OnListChangedCallback<T>(){
    override fun onChanged(p0: T) {
    }

    override fun onItemRangeChanged(p0: T, p1: Int, p2: Int) {
        adapter.notifyItemRangeChanged(p1, p2)
    }

    override fun onItemRangeMoved(p0: T, p1: Int, p2: Int, p3: Int) {
        adapter.notifyItemMoved(p2, p3)
    }

    override fun onItemRangeRemoved(p0: T, p1: Int, p2: Int) {
        adapter.notifyItemRangeRemoved(p1, p2)
    }

    override fun onItemRangeInserted(p0: T, p1: Int, p2: Int) {
        adapter.notifyItemRangeInserted(p1, p2)
    }
}