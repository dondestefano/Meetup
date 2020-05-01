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

private const val TYPE_ACCEPT_HEADER = 0
private const val TYPE_DECLINE_HEADER = 1
private const val TYPE_EVENT = 2

class EventRecycleAdapter(private val context: Context, private val events: List<Event>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_ACCEPT_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.event_header_card_layout, parent, false)
                return HeaderViewHolder(itemView, "Hej")
            }

/*            TYPE_DECLINE_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.event_header_card_layout, parent, false)
                return HeaderViewHolder(itemView, "Då")
            }*/

            else -> {
                val itemView = layoutInflater.inflate(R.layout.event_card_layout, parent, false)
                return EventViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (position) {
            0 -> {
                TYPE_ACCEPT_HEADER
            }
            events.filter { it.attend == true }.size -> {
                TYPE_DECLINE_HEADER
            }
            else -> {
                TYPE_EVENT
            }
        }
    }

    override fun getItemCount(): Int {
        var size = events.size + 1
/*        if (events.any { it.attend == false }) {
            size +=  1
        }*/
        return size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.headerNameTextView.text = holder.text
            }

            is EventViewHolder -> {
                val event = events[position - 1]
                holder.textViewName.text = event.name
                holder.textViewDate.text = (EventDataManager.dateFormat.format(event.date) + " " + EventDataManager.timeFormat.format(event.date))
                holder.eventPosition = position - 1

                holder.attendButton.setOnClickListener{
                    val currentEvent = events[position - 1]
                    currentEvent.changeAttend()

                    if (event.attend!!) {
                        holder.attendButton.setBackgroundColor(Color.GREEN)
                        holder.attendButton.setText("Yes")
                    } else {
                        holder.attendButton.setBackgroundColor(Color.RED)
                        holder.attendButton.setText("No")
                    }
                }

                if (event.attend!!) {
                    holder.attendButton.setBackgroundColor(Color.GREEN)
                    holder.attendButton.setText("Yes")
                } else {holder.attendButton.setBackgroundColor(Color.RED)
                    holder.attendButton.setText("No")}
            }
        }
    }

    fun setEventData() {

    }


    inner class EventViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val textViewName = itemView.findViewById<TextView>(R.id.textViewName)
        val textViewDate = itemView.findViewById<TextView>(R.id.textViewDate)
        val attendButton = itemView.findViewById<Button>(R.id.attendButton)
        var eventPosition = 0

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, AddAndEditEventActivity::class.java)
                intent.putExtra("EVENT_POSITION", eventPosition)
                context.startActivity(intent)
            }
        }
    }

    inner class HeaderViewHolder(itemView: View, text: String) : RecyclerView.ViewHolder(itemView)  {
        val headerNameTextView = itemView.findViewById<TextView>(R.id.eventListHeader)
        val text = text

    }
}