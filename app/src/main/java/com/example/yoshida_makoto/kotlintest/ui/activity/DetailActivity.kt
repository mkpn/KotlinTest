package com.example.yoshida_makoto.kotlintest.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.yoshida_makoto.kotlintest.R

class DetailActivity : AppCompatActivity() {

    fun createIntent(context: Context): Intent {
        return Intent(context, DetailActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }
}
