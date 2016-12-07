package com.example.yoshida_makoto.kotlintest.ui.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.dagger.Injector
import com.example.yoshida_makoto.kotlintest.databinding.PlayerActivityBinding
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.PlayerViewModel
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import io.reactivex.disposables.CompositeDisposable

/***
 * ViewModel的な役割も持たせることにした。
 * （階層的にはViewModel
 */
class PlayerActivity : AppCompatActivity() {
    val disposables = CompositeDisposable()

    companion object {
        val ARG_MUSIC_ID = "music_id"

        fun createIntent(context: Context, songId: Long): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(ARG_MUSIC_ID, songId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.component.inject(this)
        val binding = DataBindingUtil.setContentView<PlayerActivityBinding>(this, R.layout.player_activity)
        val songId = intent.getLongExtra(ARG_MUSIC_ID, 0)
        val vm = PlayerViewModel(songId)

        // vm.PlayMusicCommandみたいにするのが良さげ
        disposables.add(vm.player.musicLoadedStream.subscribe { music -> binding.vm = vm })

        binding.playerView.controllerShowTimeoutMs = -1
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)
    }
}
