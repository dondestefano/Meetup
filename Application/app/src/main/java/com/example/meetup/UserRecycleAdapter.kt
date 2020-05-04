package com.example.meetup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserRecycleAdapter(private val context: Context) : RecyclerView.Adapter<UserRecycleAdapter.UserViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var users = listOf<User>()
    private var invitedUsers = mutableListOf<User>()

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
        holder.userInviteCheckBox.setOnClickListener() {
            if(holder.userInviteCheckBox.isChecked){
                invitedUsers.add(currentUser)
            }
        }
    }

    inner class UserViewHolder(userView: View) : RecyclerView.ViewHolder(userView) {
        val nameSearchView : TextView = itemView.findViewById<TextView>(R.id.userSearchName)
        val imageView : ImageView = itemView.findViewById<ImageView>(R.id.userSearchImage)
        val userInviteCheckBox : CheckBox = itemView.findViewById<CheckBox>(R.id.inviteCheckBox)
    }


}