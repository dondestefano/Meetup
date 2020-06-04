package com.dondestefano.ugame.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.dondestefano.ugame.activities.EVENT_EXTRA
import com.dondestefano.ugame.data_managers.EventDataManager
import com.dondestefano.ugame.objects.Event
import java.util.*

object AlarmScheduler {

    fun setAlarmForEvent(context: Context, eventPosition: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = createPendingIntent(context, eventPosition)
        val event = EventDataManager.itemsList[eventPosition].event

        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
        event?.date?.let { date ->
            datetimeToAlarm.timeInMillis = System.currentTimeMillis()
            datetimeToAlarm.set(Calendar.HOUR_OF_DAY, date.hours)
            datetimeToAlarm.set(Calendar.MINUTE, date.minutes)
            datetimeToAlarm.set(Calendar.SECOND, 0)
            datetimeToAlarm.set(Calendar.MILLISECOND, 0)
            datetimeToAlarm.set(Calendar.DAY_OF_WEEK, date.day + 1)
        }
        println("!!! Set $alarmIntent")
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, datetimeToAlarm.timeInMillis,(1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)
    }

    fun removeAlarmForEvent(context: Context, eventPosition: Int) {
        val alarmIntent = createPendingIntent(context, eventPosition)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        println("!!! Removed $alarmIntent")
        alarmManager.cancel(alarmIntent)
    }

    private fun createPendingIntent(context: Context, eventPosition: Int): PendingIntent? {
        val event = EventDataManager.itemsList[eventPosition].event
        // create the intent using a unique type
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            type = "${event?.name}-${event?.host}"
            putExtra(EVENT_EXTRA, eventPosition)
        }
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun checkIfTimeValid(event: Event): Boolean {
        // Get the current time
        val now = Calendar.getInstance(Locale.getDefault())
        // Get events time
        val eventTime = Calendar.getInstance(Locale.getDefault())
        event?.date?.let {
            eventTime.set(Calendar.HOUR_OF_DAY, it.hours)
            eventTime.set(Calendar.MINUTE, it.minutes)
            eventTime.set(Calendar.SECOND, 0)
            eventTime.set(Calendar.MILLISECOND, 0)
            eventTime.set(Calendar.MONTH, it.month)
            eventTime.set(Calendar.DAY_OF_WEEK, it.day + 1) // Date days start at 0. Add one to get correct day.
        }
        // Return if the event's time hasn't passed.
        return eventTime.time >= now.time
    }
}