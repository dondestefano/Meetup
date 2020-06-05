package com.dondestefano.ugame.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import com.dondestefano.ugame.R
import com.dondestefano.ugame.activities.AddAndEditEventActivity
import com.dondestefano.ugame.activities.EVENT_EXTRA
import com.dondestefano.ugame.data_managers.EventDataManager
import com.dondestefano.ugame.objects.Event
import kotlin.properties.Delegates

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val eventPosition = intent.getIntExtra(EVENT_EXTRA, -0)
            if (eventPosition != -0) {
                val eventChannel: String = context.getString(R.string.event_channel_name)
                val event = EventDataManager.itemsList[eventPosition].event
                // Start intent for the event.
                val alarmIntent = Intent(context, AddAndEditEventActivity::class.java)
                alarmIntent.putExtra("EVENT_POSITION", eventPosition)
                event?.name?.let { NotificationHelper.createNotification(eventChannel, context, it, "Your event: ${it}, is about to start.", alarmIntent) }
            }
        }
    }
}