package com.example.yoshida_makoto.kotlintest

import android.databinding.BindingAdapter
import android.databinding.ObservableArrayList
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by yoshida_makoto on 2016/09/06.
 */
object DataBindingAttributeUtil {
    @BindingAdapter("itemList")
    @JvmStatic
    fun setItemList(view: RecyclerView, itemList: ObservableArrayList<String>) {
        if (itemList.isNotEmpty()){
            view.layoutManager = LinearLayoutManager(view.context)
            val adapter = ListAdapter(view.context, itemList)
            view.adapter = adapter
            view.adapter.notifyDataSetChanged()
        }
    }
}