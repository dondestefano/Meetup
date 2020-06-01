package com.dondestefano.ugame.recycle_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dondestefano.ugame.R
import com.dondestefano.ugame.objects.User
import com.squareup.picasso.Picasso

class GuestListRecycleAdapter(private val context: Context): RecyclerView.Adapter<GuestListRecycleAdapter.GuestListViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    private var guests = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestListViewHolder {
        val guestView = layoutInflater.inflate(R.layout.guest_list_card_layout, parent, false)
        return GuestListViewHolder(guestView)
    }

    fun updateGuestList(list: List<User>) {
        guests = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = guests.size

    override fun onBindViewHolder(holder: GuestListViewHolder, position: Int) {
        val currentGuest = guests[position]
        val uri = currentGuest.profileImageURL
        Picasso.get().load(uri).into(holder.guestImageView)
    }

    inner class GuestListViewHolder(guestView: View) : RecyclerView.ViewHolder(guestView) {
        val guestImageView: ImageView = itemView.findViewById(R.id.guestImage)
    }
}