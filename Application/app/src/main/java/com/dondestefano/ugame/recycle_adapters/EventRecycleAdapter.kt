package com.dondestefano.ugame.recycle_adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dondestefano.ugame.R
import com.dondestefano.ugame.activities.AddAndEditEventActivity
import com.dondestefano.ugame.objects.AdapterItem
import com.dondestefano.ugame.data_managers.EventDataManager
import com.dondestefano.ugame.data_managers.UserDataManager
import com.dondestefano.ugame.notification.AlarmScheduler
import com.dondestefano.ugame.notification.NotificationHelper
import com.dondestefano.ugame.objects.Event
import com.dondestefano.ugame.objects.User

const val GUEST_LIST_ATTEND = "ATTEND"
const val GUEST_LIST_DECLINED = "DECLINED"
const val GUEST_LIST_NEW = "NEW"

class EventRecycleAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    private var listItems = listOf<AdapterItem>()

    companion object {
        // Helpers to determine view type
        const val TYPE_NEW_HEADER = 0
        const val TYPE_ACCEPT_HEADER = 1
        const val TYPE_DECLINE_HEADER = 2
        const val TYPE_EVENT = 3
        // Helpers to determine what list the guestListRecycleView should display.

    }

    fun updateItemsToList(list : List<AdapterItem>) {
        listItems = list
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

                when (holder.text) {
                    "New invites!" -> {
                        holder.headerIconImageView.setImageResource(R.drawable.new_alert)
                    }
                    "Attending events" -> {
                        holder.headerIconImageView.setImageResource(R.drawable.approve)
                    }
                    "Declined events" -> {
                        holder.headerIconImageView.setImageResource(R.drawable.decline)
                    }
                }
            }

            is EventViewHolder -> {
                val currentItem = listItems[position]
                val event = currentItem.event
                val guestList = mutableListOf<User>()
                holder.textViewName.text = event?.name
                holder.textViewDate.text = (EventDataManager.dateFormat.format(event?.date) + " " + EventDataManager.timeFormat.format(event?.date))
                holder.eventPosition = position

                // If the user isn't the host enable quick attendance functionality.
                if (event?.host != UserDataManager.loggedInUser.userID) {
                    holder.attendButton.setOnClickListener{
                        currentItem.event?.changeAttend(null)
                    }
                }

                // Check attendance of guests through EventDataManager
                if (event != null) {
                    holder.setGuestRecycleAdapter(event)
                }

                when {
                    event?.host == UserDataManager.loggedInUser.userID -> {
                        holder.attendButton.setText("Hosting")
                        holder.attendButton.setTextColor(Color.WHITE)
                        holder.attendButton.isClickable = false
                        holder.attendButton.isEnabled = false
                    }

                    event?.new!! ->  {
                        holder.attendButton.setText("U Game?")
                        holder.attendButton.setTextColor(Color.YELLOW)
                        holder.attendButton.isClickable = true
                        holder.attendButton.isEnabled = true
                    }

                    event?.attend!! -> {
                        holder.attendButton.setText("I'm Game")
                        holder.attendButton.setTextColor(Color.GREEN)
                        holder.attendButton.isClickable = true
                        holder.attendButton.isEnabled = true
                        if (event != null && AlarmScheduler.checkIfTimeValid(event)) {
                            // Remove any previous instance.
                            AlarmScheduler.removeAlarmForEvent(context, position)
                            // Set new instance.
                            AlarmScheduler.setAlarmForEvent(context, position)
                        }
                    }
                    else -> {
                        holder.attendButton.setText("Can't")
                        holder.attendButton.setTextColor(Color.RED)
                        holder.attendButton.isClickable = true
                        holder.attendButton.isEnabled = true
                        // Remove any scheduled alarms for the event.
                        if (event != null) {
                            AlarmScheduler.removeAlarmForEvent(context, position)
                        }
                    }
                }
            }
        }
    }

    inner class EventViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById<TextView>(R.id.textViewName)
        val textViewDate: TextView = itemView.findViewById<TextView>(R.id.textViewDate)
        val attendButton: TextView = itemView.findViewById<Button>(R.id.attendButton)
        var eventPosition = 0
        val guestListRecyclerView = itemView.findViewById<RecyclerView>(R.id.guestListRecyclerView)

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, AddAndEditEventActivity::class.java)
                intent.putExtra("EVENT_POSITION", eventPosition)
                context.startActivity(intent)
            }
        }

        fun setGuestRecycleAdapter(event: Event) {
            guestListRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            val guestListAdapter = GuestListRecycleAdapter(context)
            guestListRecyclerView.adapter = guestListAdapter
            EventDataManager.checkAttendance(event, guestListRecyclerView, guestListAdapter, GUEST_LIST_ATTEND)
        }
    }

    inner class HeaderViewHolder(itemView: View, text: String) : RecyclerView.ViewHolder(itemView)  {
        val headerNameTextView: TextView = itemView.findViewById<TextView>(R.id.eventListHeader)
        val headerIconImageView: ImageView = itemView.findViewById(R.id.headerIconImageView)
        val text = text
    }
}