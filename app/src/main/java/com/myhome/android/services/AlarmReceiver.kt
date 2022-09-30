package com.myhome.android.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.R
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, p1: Intent?) {
        val notificationManager =
            getSystemService(context, NotificationManager::class.java) as NotificationManager

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("notification")
            .setContentText("message")
            .setSmallIcon(R.drawable.notification_icon_background)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {

        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val NOTIFICATION_ID = 1

        fun newIntent(context: Context): Intent {
            return Intent(context, AlarmReceiver::class.java)
        }
    }
}