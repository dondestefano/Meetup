package com.example.meetup.recycle_adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.objects.AdapterItem
import com.example.meetup.R
import com.example.meetup.activites.UserProfileActivity

class FriendRecycleAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    private var listItems = listOf<AdapterItem>()

    companion object {
        const val TYPE_FRIEND_HEADER = 0
        const val TYPE_WAITING_HEADER = 1
        const val TYPE_FRIEND = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_FRIEND_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.header_card_layout, parent, false)
                return HeaderViewHolder(itemView, "Friends")
            }

            TYPE_WAITING_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.header_card_layout, parent, false)
                return HeaderViewHolder(itemView, "Pending Requests")
            }

            else -> {
                val itemView = layoutInflater.inflate(R.layout.user_blank_card_layout, parent, false)
                return FriendViewHolder(itemView)
            }
        }
    }

    override fun getItemCount() = listItems.size

    override fun getItemViewType(position: Int): Int = listItems[position].viewType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.headerNameTextView.text = holder.text
            }

            is FriendViewHolder -> {
                val currentItem = listItems[position]
                val currentFriend = currentItem.user
                holder.friendPosition = position
                currentFriend?.name.let {holder.nameView.text = it}
            }
        }
    }

    fun updateItemsToList(list : List<AdapterItem>) {
        listItems = list
        notifyDataSetChanged()
    }

    inner class FriendViewHolder(userView: View) : RecyclerView.ViewHolder(userView) {
        val nameView: TextView = itemView.findViewById<TextView>(R.id.friendName)
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.friendImage)
        var friendPosition = 0
        init {
            itemView.setOnClickListener {
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("USER_POSITION", friendPosition)
                context.startActivity(intent)
            }
        }
    }

    inner class HeaderViewHolder(itemView: View, text: String) :
        RecyclerView.ViewHolder(itemView) {
        val headerNameTextView: TextView = itemView.findViewById<TextView>(R.id.eventListHeader)
        val text = text
    }
}