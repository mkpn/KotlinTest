package com.example.yoshida_makoto.kotlintest.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.RelativeLayout
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.databinding.AddPlayListMenuDialogBinding

/**
 * Created by yoshida_makoto on 2017/02/15.
 */
class AddPlayListMenuDialog(context: Context) : Dialog(context, android.R.style.Theme_Dialog) {
    val inflater = LayoutInflater.from(context)

    init {
        val binding = AddPlayListMenuDialogBinding.bind(inflater.inflate(R.layout.add_play_list_menu_dialog, null))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        setContentView(binding.root)
        setCancelable(true)
        create()
    }
}