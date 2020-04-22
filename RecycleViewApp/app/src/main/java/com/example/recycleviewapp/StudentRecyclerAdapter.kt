package com.example.recycleviewapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class StudentRecyclerAdapter(private val context : Context, private val students: List<Student>) :
    RecyclerView.Adapter<StudentRecyclerAdapter.ViewHolder>() {
    private val layoutInflator = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflator.inflate(R.layout.student_list_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = students.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.textName.text = student.name
        holder.textClassName.text = student.className
        holder.studentPosition = position

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.textName)
        val textClassName = itemView.findViewById<TextView>(R.id.textClassName)
        var studentPosition = 0

        init {
            itemView.setOnClickListener{
                val intent = Intent(context, AddAndCreateStudentActivity::class.java)
                intent.putExtra("STUDENT_POSITION", studentPosition)
                context.startActivity(intent)
            }
        }
    }


}