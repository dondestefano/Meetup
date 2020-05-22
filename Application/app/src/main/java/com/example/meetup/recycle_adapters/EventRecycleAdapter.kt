package com.example.meetup.recycle_adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.activities.AddAndEditEventActivity
import com.example.meetup.objects.AdapterItem
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.R

class EventRecycleAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var listItems = listOf<AdapterItem>()
    companion object {
        const val TYPE_NEW_HEADER = 0
        const val TYPE_ACCEPT_HEADER = 1
        const val TYPE_DECLINE_HEADER = 2
        const val TYPE_EVENT = 3
    }

    fun updateItemsToList(list : List<AdapterItem>) {
        listItems = list
        for(item in list) {
            val name = item.event?.name
            println("!!! event")
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_NEW_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.header_card_layout, parent, false)
                return HeaderViewHolder(itemView, "New invites!")
            }

            TYPE_ACCEPT_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.header_card_layout, parent, false)
                return HeaderViewHolder(itemView, "Attending events")
            }

            TYPE_DECLINE_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.header_card_layout, parent, false)
                return HeaderViewHolder(itemView, "Declined events")
            }

            else -> {
                val itemView = layoutInflater.inflate(R.layout.event_card_layout, parent, false)
                return EventViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = listItems[position].viewType

    override fun getItemCount() = listItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.headerNameTextView.text = holder.text
            }

            is EventViewHolder -> {
                val currentItem = listItems[position]
                val event = currentItem.event
                holder.textViewName.text = event?.name
                holder.textViewDate.text = (EventDataManager.dateFormat.format(event?.date) + " " + EventDataManager.timeFormat.format(event?.date))
                holder.eventPosition = position

                holder.attendButton.setOnClickListener{

                    currentItem.event?.changeAttend()

                    if (event?.attend!!) {
                        holder.attendButton.setBackgroundColor(Color.GREEN)
                        holder.attendButton.setText("Yes")
                    } else {
                        holder.attendButton.setBackgroundColor(Color.RED)
                        holder.attendButton.setText("No")
                    }
                }

                if (event?.new!!) {
                    holder.attendButton.setBackgroundColor(Color.GRAY)
                    holder.attendButton.setText("U GAME?")
                }
                else if (event?.attend!!) {
                    holder.attendButton.setBackgroundColor(Color.GREEN)
                    holder.attendButton.setText("Yes")
                } else {holder.attendButton.setBackgroundColor(Color.RED)
                    holder.attendButton.setText("No")}
            }
        }
    }

    inner class EventViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById<TextView>(R.id.textViewName)
        val textViewDate: TextView = itemView.findViewById<TextView>(R.id.textViewDate)
        val attendButton: TextView = itemView.findViewById<Button>(R.id.attendButton)
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
        val headerNameTextView: TextView = itemView.findViewById<TextView>(R.id.eventListHeader)
        val text = text
    }
}