package com.example.meetup.recycle_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.R
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.data_managers.FriendDataManager
import com.example.meetup.objects.User
import com.squareup.picasso.Picasso

class InviteRecycleAdapter(private val context: Context) : RecyclerView.Adapter<InviteRecycleAdapter.UserViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var users = listOf<User>()

    override fun getItemCount() = users.size

    fun updateItemsToList(list: MutableList<User>) {
        users = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val userView = layoutInflater.inflate(R.layout.user_invite_card_layout, parent, false)
        return UserViewHolder(userView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = users[position]
        holder.nameSearchView.text = currentUser.name
        val uri = currentUser.profileImageURL
        Picasso.get().load(uri).into(holder.imageView)
        holder.userInviteCheckBox.setOnClickListener() {
            if(holder.userInviteCheckBox.isChecked){
                currentUser.userID?.let { EventDataManager.inviteList.add(it) }
            } else { EventDataManager.inviteList.remove(currentUser.userID) }
        }
    }

    inner class UserViewHolder(userView: View) : RecyclerView.ViewHolder(userView) {
        val nameSearchView : TextView = itemView.findViewById<TextView>(R.id.userSearchName)
        val imageView : ImageView = itemView.findViewById<ImageView>(R.id.userSearchImage)
        val userInviteCheckBox : CheckBox = itemView.findViewById<CheckBox>(R.id.inviteCheckBox)
    }
}