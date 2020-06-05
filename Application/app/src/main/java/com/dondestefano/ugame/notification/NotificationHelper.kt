package com.dondestefano.ugame.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.dondestefano.ugame.R
import com.dondestefano.ugame.activities.MainActivity

object NotificationHelper {

    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(id: String, context: Context, title: String, message: String, intent: Intent) {
        val channelId = "${context.packageName}-$id"

        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_ugame_notification_icon)
            color = ContextCompat.getColor(context, R.color.colorAccent)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(true)

            var pendingIntent: PendingIntent
            val intentMain = Intent(context, MainActivity::class.java)
            //Check if the intent leads to the starting page. If so use intentMain.
            intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            //Check if the intent leads to the starting page.
            pendingIntent = if (intentMain == intent) {
                PendingIntent.getActivity(context, 0, intent, 0)
            } else {
                // If not add it to an array with the starting page.
                val chainIntent = arrayOf(intentMain, intent)
                PendingIntent.getActivities(context, 0, chainIntent, PendingIntent.FLAG_ONE_SHOT)
            }

            setContentIntent(pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationBuilder.build())
    }
}