package com.dondestefano.ugame.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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
                val event = EventDataManager.itemsList[eventPosition].event
                // Start intent for the event.
                val intent = Intent(context, AddAndEditEventActivity::class.java)
                intent.putExtra("EVENT_POSITION", eventPosition)
                event?.name?.let { NotificationHelper.createNotification("Event", context, it, "Your event: ${it}, is about to start.", intent) }
            }
        }
    }
}