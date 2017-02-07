package com.example.yoshida_makoto.kotlintest

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import com.example.yoshida_makoto.kotlintest.di.Injector
import com.example.yoshida_makoto.kotlintest.value.PlayerServiceValues
import com.example.yoshida_makoto.kotlintest.value.PlayerServiceValues.Companion.PLAY_NEXT
import com.example.yoshida_makoto.kotlintest.value.PlayerServiceValues.Companion.PLAY_PAUSE
import com.example.yoshida_makoto.kotlintest.value.PlayerServiceValues.Companion.PLAY_PREVIOUS
import com.example.yoshida_makoto.kotlintest.value.PlayerServiceValues.Companion.SHOW_NOTIFICATION
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


/**
 * Created by yoshida_makoto on 2016/12/27.
 */

class NotificationPlayerPanelService : Service() {
    @Inject
    lateinit var player: Player
    val disposable = CompositeDisposable()

    override fun onCreate() {
        Injector.component.inject(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        disposable.addAll(player.isPlaying.subscribe { isPlaying ->
            showNotification()
        })

        when (intent.action) {
            SHOW_NOTIFICATION -> {
                showNotification()
            }
            PLAY_NEXT -> {
                player.skipToNext()
            }
            PLAY_PREVIOUS -> {
                player.goToHeadOrPrevious()
            }
            PLAY_PAUSE -> {
                player.playOrPause()
            }
            else -> {
            }
        }
        return START_STICKY
    }

    fun showNotification() {
        val views = RemoteViews(packageName, R.layout.notification_player_layout)
        val bigViews = RemoteViews(packageName, R.layout.big_notification_player_layout)

        val skipToNextIntent = Intent(this, NotificationPlayerPanelService::class.java)
        skipToNextIntent.action = PlayerServiceValues.PLAY_NEXT
        val skipToNextPendingIntent = PendingIntent.getService(this, 0, skipToNextIntent, 0)

        val skipToPreviousIntent = Intent(this, NotificationPlayerPanelService::class.java)
        skipToPreviousIntent.action = PlayerServiceValues.PLAY_PREVIOUS
        val skipToPreviousPendingIntent = PendingIntent.getService(this, 0, skipToPreviousIntent, 0)

        val playOrPauseIntent = Intent(this, NotificationPlayerPanelService::class.java)
        playOrPauseIntent.action = PlayerServiceValues.PLAY_PAUSE
        val playOrPausePendingIntent = PendingIntent.getService(this, 0, playOrPauseIntent, 0)

        views.setOnClickPendingIntent(R.id.next_button, skipToNextPendingIntent)
        bigViews.setOnClickPendingIntent(R.id.next_button, skipToNextPendingIntent)

        views.setOnClickPendingIntent(R.id.previous_button, skipToPreviousPendingIntent)
        bigViews.setOnClickPendingIntent(R.id.previous_button, skipToPreviousPendingIntent)

        views.setOnClickPendingIntent(R.id.play_button, playOrPausePendingIntent)
        bigViews.setOnClickPendingIntent(R.id.play_button, playOrPausePendingIntent)

        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_repeat_one_white_36dp)
                .setContentText("テキスト")
                .setContentTitle("タイトル")
                .build()

//        val notification = NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_repeat_one_white_36dp)
//                .setCustomContentView(views)
//                .setVibrate(longArrayOf(0))
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .build()

//        startForeground(NOTIFICATION_ID, notification)
    }
}
