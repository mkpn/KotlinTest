package com.example.yoshida_makoto.kotlintest.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.example.yoshida_makoto.kotlintest.ObservableListCallback
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.R.layout.activity_main
import com.example.yoshida_makoto.kotlintest.databinding.ActivityMainBinding
import com.example.yoshida_makoto.kotlintest.entity.Song
import com.example.yoshida_makoto.kotlintest.events.ClickSongEvent
import com.example.yoshida_makoto.kotlintest.events.TransitToDetailEvent
import com.example.yoshida_makoto.kotlintest.ui.adapter.SongListAdapter
import com.example.yoshida_makoto.kotlintest.ui.decoration.DividerItemDecoration
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity(), android.support.v7.widget.SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        songs.clear()
        findSongList(newText ?: "")
        return false
    }

    private val songs: ObservableArrayList<Song> = ObservableArrayList()
    lateinit private var binding: ActivityMainBinding
    private val permissionCheck by lazy { ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) }
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1
    lateinit private var adapter: SongListAdapter

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, activity_main)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            showSongList()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun showSongList() {
        findSongList("")
        adapter = SongListAdapter(this, songs)
        songs.addOnListChangedCallback(ObservableListCallback(adapter))
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST))
    }

    fun findSongList(query: String) {
        val musicResolver = contentResolver
        val musicUri = EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist: String = musicCursor.getString(artistColumn) ?: "no data"
                val song = Song(thisId, thisTitle, thisArtist)
                if (query.isEmpty() || song.isContains(query)) songs.add(song)
            } while (musicCursor.moveToNext())
            musicCursor.close()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showSongList()
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchView = menu!!.findItem(R.id.action_search).actionView as android.support.v7.widget.SearchView
        searchView.setOnQueryTextListener(this)

        return true
    }

    @Subscribe
    fun onTransitToDetailEvent(event: TransitToDetailEvent) {
        startActivity(Intent(this, DetailActivity::class.java))
    }

    @Subscribe
    fun onSongClickEvent(event: ClickSongEvent) {
        startActivity(PlayerActivity().createIntent(this, event.songId))
    }
}
