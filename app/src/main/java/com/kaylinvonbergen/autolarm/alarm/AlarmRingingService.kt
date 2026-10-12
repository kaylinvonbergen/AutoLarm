
package com.kaylinvonbergen.autolarm.alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

import com.kaylinvonbergen.autolarm.R

class AlarmRingingService : Service() {

    companion object {
        const val ACTION_START =
            "com.kaylinvonbergen.autolarm.START_RINGING"

        const val ACTION_DISMISS =
            "com.kaylinvonbergen.autolarm.DISMISS_RINGING"

        const val EXTRA_ALARM_ID = "alarm_id"

        const val CHANNEL_ID = "autolarm_ringing_v1"
        const val NOTIFICATION_ID = 9001

        fun createNotificationChannel(context: Context) {
            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ringing alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Active AutoLarm wake-up alarms"
                lockscreenVisibility =
                    Notification.VISIBILITY_PUBLIC

                // MediaPlayer handles alarm audio.
                // Don't play a second notification sound.
                setSound(null, null)
                enableVibration(false)
            }

            manager.createNotificationChannel(channel)
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var activeAlarmId: String? = null

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= 31) {
            val manager = getSystemService(
                Context.VIBRATOR_MANAGER_SERVICE
            ) as VibratorManager

            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        when (intent?.action) {
            ACTION_START -> {
                val id = intent.getStringExtra(
                    EXTRA_ALARM_ID
                ) ?: return START_NOT_STICKY

                startRinging(id)
            }

            ACTION_DISMISS -> {
                val id = intent.getStringExtra(
                    EXTRA_ALARM_ID
                )

                // Don't let an old dismiss intent silence
                // a newer, different alarm.
                if (id == activeAlarmId) {
                    stopRinging()
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun startRinging(id: String) {
        if (activeAlarmId == id && mediaPlayer != null) {
            return
        }

        // Stop any previous ringing session.
        stopPlayback()

        activeAlarmId = id

        val notification = buildNotification(id)

        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                notification
            )
        }

        playAlarmSound()
        startVibration()

        Log.d("AutoLarm", "Ringing started: $id")
    }

    private fun buildNotification(id: String): Notification {
        val screenIntent = Intent(
            this,
            RingingActivity::class.java
        ).apply {
            putExtra(EXTRA_ALARM_ID, id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val screenPendingIntent = PendingIntent.getActivity(
            this,
            1001,
            screenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(
            this,
            AlarmRingingService::class.java
        ).apply {
            action = ACTION_DISMISS
            putExtra(EXTRA_ALARM_ID, id)
        }

        val dismissPendingIntent = PendingIntent.getService(
            this,
            1002,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_autolarm)
            .setContentTitle("Time to get up!")
            .setContentText("Your AutoLarm is ringing.")
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(Notification.PRIORITY_MAX)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(screenPendingIntent)
            .setFullScreenIntent(screenPendingIntent, true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Dismiss",
                dismissPendingIntent
            )
            .build()
    }

    private fun playAlarmSound() {
        try {
            val uri = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_ALARM
            ) ?: RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
            )

            if (uri == null) {
                Log.w("AutoLarm", "No system alarm sound")
                return
            }

            val player = MediaPlayer()

            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(
                        AudioAttributes.CONTENT_TYPE_SONIFICATION
                    )
                    .build()
            )

            player.setDataSource(this, uri)
            player.isLooping = true
            player.prepare()
            player.start()

            mediaPlayer = player
        } catch (e: Exception) {
            Log.e(
                "AutoLarm",
                "Failed to play alarm sound",
                e
            )
        }
    }

    private fun startVibration() {
        try {
            if (!vibrator.hasVibrator()) return

            val pattern = longArrayOf(
                0, 500, 300, 500, 800
            )

            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        pattern,
                        0
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            Log.e("AutoLarm", "Vibration failed", e)
        }
    }

    private fun stopPlayback() {
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
        }

        mediaPlayer?.release()
        mediaPlayer = null

        vibrator.cancel()
    }

    private fun stopRinging() {
        stopPlayback()
        activeAlarmId = null

        if (Build.VERSION.SDK_INT >= 24) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        val manager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        manager.cancel(NOTIFICATION_ID)
    }

    override fun onDestroy() {
        stopPlayback()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
