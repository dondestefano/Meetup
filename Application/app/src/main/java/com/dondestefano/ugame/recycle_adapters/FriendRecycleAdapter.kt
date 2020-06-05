package com.dondestefano.ugame.recycle_adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dondestefano.ugame.R
import com.dondestefano.ugame.objects.AdapterItem
import com.dondestefano.ugame.activities.UserProfileActivity
import com.squareup.picasso.Picasso

class FriendRecycleAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    private var listItems = listOf<AdapterItem>()

    companion object {
        const val TYPE_FRIEND_HEADER = 0
        const val TYPE_REQUESTED_HEADER = 1
        const val TYPE_WAITING_HEADER = 2
        const val TYPE_FRIEND = 3
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_FRIEND_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.header_card_layout, parent, false)
                return HeaderViewHolder(itemView, context.getString(R.string.friend_list_title))
            }

            TYPE_REQUESTED_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.header_card_layout, parent, false)
                return HeaderViewHolder(itemView, context.getString(R.string.new_friend_list_title))
            }

            TYPE_WAITING_HEADER -> {
                val itemView = layoutInflater.inflate(R.layout.header_card_layout, parent, false)
                return HeaderViewHolder(itemView, context.getString(R.string.sent_request_list_title))
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
                when (holder.text) {
                    context.getString(R.string.friend_list_title) -> {
                        holder.headerIconImageView.setImageResource(R.drawable.friends)
                    }

                    context.getString(R.string.new_friend_list_title) -> {
                        holder.headerIconImageView.setImageResource(R.drawable.new_alert)
                    }

                    context.getString(R.string.sent_request_list_title) -> {
                        holder.headerIconImageView.setImageResource(R.drawable.sent)
                    }
                }
            }

            is FriendViewHolder -> {
                val currentItem = listItems[position]
                val currentFriend = currentItem.user
                holder.userID = currentFriend?.userID
                currentFriend?.name.let {holder.nameView.text = it}
                val uri = currentFriend?.profileImageURL
                Picasso.get().load(uri).into(holder.imageView)
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
        var userID: String? = null
        init {
            itemView.setOnClickListener {
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("USER_ID", userID)
                context.startActivity(intent)
            }
        }
    }

    inner class HeaderViewHolder(itemView: View, text: String) :
        RecyclerView.ViewHolder(itemView) {
        val headerNameTextView: TextView = itemView.findViewById<TextView>(R.id.eventListHeader)
        val headerIconImageView: ImageView = itemView.findViewById(R.id.headerIconImageView)
        val text = text

    }
}