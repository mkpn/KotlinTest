package com.example.yoshida_makoto.kotlintest

import android.content.Intent
import android.database.Cursor
import android.database.Observable
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.provider.MediaStore.Audio.Media.*
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.yoshida_makoto.kotlintest.R.layout.activity_main
import com.example.yoshida_makoto.kotlintest.databinding.ActivityMainBinding
import com.example.yoshida_makoto.kotlintest.entity.Song
import com.example.yoshida_makoto.kotlintest.events.TransitToDetailEvent
import com.example.yoshida_makoto.kotlintest.viewmodel.ScheduleViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {
    private val mBinding: ActivityMainBinding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, activity_main) }

    override protected fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(activity_main)
    }

    override protected fun onStart() {
        super.onStart()
        val vm = ScheduleViewModel("12:00", "lunch")
        mBinding.vm = vm;
        mBinding.button.setOnClickListener({
            vm.time.set("15:00")
            vm.task.set("sleep")
        })

        val observableArrayList = ObservableArrayList<String>()
        mBinding.itemList = observableArrayList
    }

    fun setItemList(view: RecyclerView, itemList: ObservableArrayList<String>) {
        if (itemList.isNotEmpty()){
            view.layoutManager = LinearLayoutManager(view.context)
            val adapter = ListAdapter(view.context, itemList)
            view.adapter = adapter
            view.adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    fun getSongList() : ObservableArrayList<Song>{
        val musicResolver = contentResolver
        val musicUri = EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        val songs : ObservableArrayList<Song> = ObservableArrayList()
        if (musicCursor != null && musicCursor.moveToFirst()){
            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                songs.add(Song(thisId, thisTitle, thisArtist))
            }while (musicCursor.moveToNext())
        }
        return songs
    }

    @Subscribe
    public fun onTransitToDetailEvent(event: TransitToDetailEvent) {
        startActivity(Intent(this, DetailActivity::class.java))
    }
}
