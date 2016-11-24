package com.example.yoshida_makoto.kotlintest.ui.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.yoshida_makoto.kotlintest.MyApplication
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.databinding.PlayerActivityBinding
import com.example.yoshida_makoto.kotlintest.ui.viewmodel.PlayerViewModel
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PlayerActivity : AppCompatActivity() {
    @Inject
    lateinit var player: Player

    val disposables = CompositeDisposable()

    companion object {
        fun createIntent(context: Context, songId: Long): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("song_id", songId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)
        (application as MyApplication).applicationComponent.inject(this)
        val binding = DataBindingUtil.setContentView<PlayerActivityBinding>(this, R.layout.player_activity)
        val vm = PlayerViewModel()
        disposables.add(vm.pitchChangeObservable.subscribe { pitchDifference -> player.changePitch(pitchDifference) })

        binding.pitchDown.setOnClickListener { player.changePitch(-1) }
        binding.pitchUp.setOnClickListener { player.changePitch(1) }

        player.setPlayerView(binding.playerView)
        binding.playerView.controllerShowTimeoutMs = -1
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)
        val songId = intent.getLongExtra("song_id", 0)

        player.playMusic(songId)

        binding.key = player.key
    }
}
