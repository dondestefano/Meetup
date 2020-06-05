package com.dondestefano.ugame.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dondestefano.ugame.R
import com.dondestefano.ugame.activities.SearchUsersActivity
import com.dondestefano.ugame.data_managers.FriendDataManager
import com.dondestefano.ugame.recycle_adapters.FriendRecycleAdapter
import com.google.firebase.auth.FirebaseAuth

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FriendsListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var friendRecyclerView : RecyclerView? = null
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_list, container, false)
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            FriendsListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        FriendDataManager.resetFriendDataManagerUser()
        setFriendRecycleAdapters()
        setFabButtons()
        friendRecyclerView?.let { FriendDataManager.setFirebaseListenerForFriends(it) }
    }

    private fun setFriendRecycleAdapters() {
        friendRecyclerView = view?.findViewById<RecyclerView>(R.id.friendRecyclerView)
        friendRecyclerView?.layoutManager = LinearLayoutManager(activity)

        val friendAdapter = activity?.let { FriendRecycleAdapter(it) }
        friendAdapter?.updateItemsToList(FriendDataManager.itemsList)
        friendRecyclerView?.adapter = friendAdapter
    }

    private fun setFabButtons() {
        val fab = view?.findViewById<View>(R.id.addFriendActionButton)
        fab?.setOnClickListener {
            val intent = Intent(activity, SearchUsersActivity::class.java)
            startActivity(intent)
        }
    }
}
