package com.example.recycleviewapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById<RecyclerView>(R.id.studentList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = StudentRecyclerAdapter(this, DataManager.students)

        val fab = findViewById<View>(R.id.floatingActionButton3)
        fab.setOnClickListener{ view ->
            val intent = Intent(this, AddAndCreateStudentActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        recyclerView.adapter?.notifyDataSetChanged()
    }

}
