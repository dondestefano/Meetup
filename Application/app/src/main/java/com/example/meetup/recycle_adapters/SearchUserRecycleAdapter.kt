package com.example.meetup.recycle_adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.activites.UserProfileActivity
import com.example.meetup.objects.User
import com.example.meetup.R

class SearchUserRecycleAdapter(private val context: Context) : RecyclerView.Adapter<SearchUserRecycleAdapter.SearchUserViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var users = listOf<User>()

    override fun getItemCount() = users.size

    fun updateItemsToList(list: MutableList<User>) {
        users = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        val userView = layoutInflater.inflate(R.layout.user_blank_card_layout, parent, false)
        return SearchUserViewHolder(userView)
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val currentUser = users[position]
        holder.nameSearchView.text = currentUser.name
        holder.userPosition = position
    }

    inner class SearchUserViewHolder(userView: View) : RecyclerView.ViewHolder(userView) {
        val nameSearchView: TextView = itemView.findViewById<TextView>(R.id.friendName)
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.friendImage)
        var userPosition = 0

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("USER_POSITION", userPosition)
                context.startActivity(intent)
            }
        }
    }
}