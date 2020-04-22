package com.example.meetup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class EventRecycleAdapter(private val context : Context, private val events: List<Event>) : RecyclerView.Adapter<EventRecycleAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.event_card_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.textViewName.text = event.name
        holder.textViewDate.text = (EventDataManager.dateFormat.format(event.date) + " " + EventDataManager.timeFormat.format(event.date))

        holder.attendButton.setOnClickListener{
            val currentEvent = events[position]
            currentEvent.changeAttend()

            if (event.attend) {
                holder.attendButton.setBackgroundColor(Color.GREEN)
                holder.attendButton.setText("Yes")
                addEventToAttending(position)
            } else {
                holder.attendButton.setBackgroundColor(Color.RED)
                holder.attendButton.setText("No")
                addEventToDeclined(position)
            }
        }

        if (event.attend) {
            holder.attendButton.setBackgroundColor(Color.GREEN)
            holder.attendButton.setText("Yes")
        } else {holder.attendButton.setBackgroundColor(Color.RED)
            holder.attendButton.setText("No")}
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val textViewName = itemView.findViewById<TextView>(R.id.textViewName)
        val textViewDate = itemView.findViewById<TextView>(R.id.textViewDate)
        val attendButton = itemView.findViewById<Button>(R.id.attendButton)
    }

    fun addEventToAttending(position: Int) {
        val event = events[position]
        val dataManagerEvent = EventDataManager.declinedEvents[position]

        if (dataManagerEvent.name == event.name) {
            EventDataManager.declinedEvents.removeAt(position)
            EventDataManager.attendingEvents.add(event)
        } else {
            EventDataManager.attendingEvents.add(event)
        }
        notifyDataSetChanged()
    }

    fun addEventToDeclined(position: Int) {
        val event = events[position]
        val dataManagerEvent = EventDataManager.attendingEvents[position]

        if (dataManagerEvent.name == event.name) {
            EventDataManager.attendingEvents.removeAt(position)
            EventDataManager.declinedEvents.add(event)
        } else {
            EventDataManager.declinedEvents.add(event)
        }
        notifyDataSetChanged()
    }

}