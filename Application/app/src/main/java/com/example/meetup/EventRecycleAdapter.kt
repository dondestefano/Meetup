package com.example.meetup

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventRecycleAdapter(private val context: Context, private val events: List<Event>, private var otherAdapter: EventRecycleAdapter?) : RecyclerView.Adapter<EventRecycleAdapter.ViewHolder>() {

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
        holder.eventPosition = position

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

    fun addEventToAttending(position: Int) {
        val event = events[position]
        val dataManagerEvent = EventDataManager.declinedEvents[position]

        if (dataManagerEvent.name == event.name) {
            EventDataManager.declinedEvents.removeAt(position)
            EventDataManager.attendingEvents.add(event)
        } else {
            EventDataManager.attendingEvents.add(event)
        }
        EventDataManager.sortLists()
        updateRecycleView()
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
        EventDataManager.sortLists()
        updateRecycleView()
    }

    fun setOtherAdapter(adapter : EventRecycleAdapter){
        otherAdapter = adapter
    }

    fun updateRecycleView() {
        notifyDataSetChanged()
        otherAdapter?.notifyDataSetChanged()
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val textViewName = itemView.findViewById<TextView>(R.id.textViewName)
        val textViewDate = itemView.findViewById<TextView>(R.id.textViewDate)
        val attendButton = itemView.findViewById<Button>(R.id.attendButton)
        var eventPosition = 0

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, AddAndEditEventActivity::class.java)
                intent.putExtra("EVENT_POSITION", eventPosition)
                Log.d("hej", eventPosition.toString())
                if (events == EventDataManager.attendingEvents) {
                    intent.putExtra("EVENT_LIST", "attending")
                } else {
                    intent.putExtra("EVENT_LIST", "declined")
                }
                context.startActivity(intent)
            }
        }
    }
}