package com.example.yoshida_makoto.kotlintest

import com.google.android.exoplayer.ExoPlaybackException
import com.google.android.exoplayer.ExoPlayer

/**
 * Created by yoshida_makoto on 2016/10/20.
 */
class Player : ExoPlayer.Listener{

    override fun onPlayerError(error: ExoPlaybackException?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayWhenReadyCommitted() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}