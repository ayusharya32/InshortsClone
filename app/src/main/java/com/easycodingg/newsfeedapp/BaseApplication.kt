package com.easycodingg.newsfeedapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.easycodingg.newsfeedapp.util.Constants.CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_ID,
                "Channel 1",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel1)
        }
    }
}