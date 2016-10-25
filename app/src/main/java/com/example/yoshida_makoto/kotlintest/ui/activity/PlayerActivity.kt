package com.example.yoshida_makoto.kotlintest.ui.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.yoshida_makoto.kotlintest.Player
import com.example.yoshida_makoto.kotlintest.R
import com.example.yoshida_makoto.kotlintest.databinding.PlayerActivityBinding

class PlayerActivity : AppCompatActivity() {
    val player: Player = Player(this)

    fun createIntent(context: Context, songId: Long): Intent {
        intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("song_id", songId)
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)

        val binding = DataBindingUtil.setContentView<PlayerActivityBinding>(this, R.layout.player_activity);
        binding.pitchDown.setOnClickListener { player.sendChangePitchMessage(-1) }
        binding.pitchUp.setOnClickListener { player.sendChangePitchMessage(1) }
        binding.start.setOnClickListener { player.start() }
        binding.stop.setOnClickListener { player.stop() }
        val songId = intent.getLongExtra("song_id", 0)

        player.playSong(songId)
    }
}
