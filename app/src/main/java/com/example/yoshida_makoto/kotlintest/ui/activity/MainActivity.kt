package com.example.yoshida_makoto.kotlintest.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.databinding.ActivityMainBinding
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.ui.fragment.MusicListFragment
import com.example.yoshida_makoto.kotlintest.ui.fragment.PlayerFragment
import com.example.yoshida_makoto.kotlintest.ui.fragment.UserPlayListFragment
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.MainViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.disposables.CompositeDisposable
import permissions.dispatcher.*

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    val disposables = CompositeDisposable()
    lateinit var playerFragment: PlayerFragment
    lateinit var musicListFragment: MusicListFragment
    lateinit var userPlayListFragment: UserPlayListFragment
    val tabTitles = arrayOf("Musics", "PlayLists")
    lateinit private var binding: ActivityMainBinding
    private val permissionCheck by lazy { ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) }
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1

    val mainVm = MainViewModel()

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityPermissionsDispatcher.initializeWithCheck(this)
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

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                musicListFragment.searchMusicByStringQuery(query)
                return false
            }
        })
        return true
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun initialize() {
        Injector.component.inject(this)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        musicListFragment = MusicListFragment.newInstance()
        userPlayListFragment = UserPlayListFragment.newInstance()
        val fragments = arrayOf(musicListFragment, userPlayListFragment)

        val adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return tabTitles[position]
            }
        }

        binding.pager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.pager)

        playerFragment = PlayerFragment.newInstance()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.player_container, playerFragment)
                .commit()
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showBar(request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setMessage("許可してちょ")
                .setPositiveButton("許可しちゃう！", { dialog, button -> request.proceed() })
                .setNegativeButton("ざけんな", { dialog, button -> request.cancel() })
                .show()
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showHoge() {

    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showFuga() {

    }

    override fun onBackPressed() {
        when (binding.slidingUpPanel.panelState) {
            SlidingUpPanelLayout.PanelState.EXPANDED -> {
                binding.slidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}


