package com.example.meetup.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.R
import com.example.meetup.activities.AddAndEditEventActivity
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.recycle_adapters.EventRecycleAdapter
import com.google.firebase.auth.FirebaseAuth


private const val ARG_PARAM3 = "param1"
private const val ARG_PARAM4 = "param2"


class EventListFragment : Fragment() {
    var eventRecyclerView : RecyclerView? = null
    lateinit var auth: FirebaseAuth
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM3)
            param2 = it.getString(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setEventRecycleAdapters()
        setFabButtons()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM3, param1)
                    putString(ARG_PARAM4, param2)
                }
            }

    }

    private fun setEventRecycleAdapters() {
        eventRecyclerView = view?.findViewById<RecyclerView>(R.id.attendRecyclerView)
        eventRecyclerView?.layoutManager = LinearLayoutManager(activity)

        val eventAdapter = activity?.let { EventRecycleAdapter(it) }
        eventAdapter?.updateItemsToList(EventDataManager.itemsList)
        eventRecyclerView?.adapter = eventAdapter

        eventRecyclerView?.let { EventDataManager.setFirebaseListener(it) }
    }

    private fun setFabButtons() {
        val fab = view?.findViewById<View>(R.id.addEventActionButton)
        fab?.setOnClickListener{
            val intent = Intent(activity, AddAndEditEventActivity::class.java)
            intent.putExtra("EVENT_POSITION", "NO_LIST")
            startActivity(intent)
        }
    }
}
