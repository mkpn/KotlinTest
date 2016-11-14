package com.example.yoshida_makoto.kotlintest.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import com.example.yoshida_makoto.kotlintest.MyApplication
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.databinding.ActivityMainBinding
import com.example.yoshida_makoto.kotlintest.messages.ClickMusicMessage
import com.example.yoshida_makoto.kotlintest.repository.MusicRepository
import com.example.yoshida_makoto.kotlintest.ui.adapter.MusicListAdapter
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MainViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var musicRepository: MusicRepository

    val subscriptions = CompositeSubscription()
    lateinit private var binding: ActivityMainBinding
    private val permissionCheck by lazy { ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) }
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1
    lateinit private var adapter: MusicListAdapter

    val mainVm by lazy { MainViewModel((application as MyApplication).applicationComponent) }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        (application as MyApplication).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        subscriptions.add(
                mainVm.messenger
                        .register(ClickMusicMessage::class.java)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ message ->
                            PlayerActivity().createIntent(this, message.songId)
                        })
        )
    }

    override fun onStart() {
        super.onStart()

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            binding.vm = mainVm
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.vm = mainVm
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
        val searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(mainVm.textChangeListener)
        return true
    }
}
