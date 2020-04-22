package com.example.recycleviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

const val POSITON_NOT_SET = -1
const val STUDENT_POSITION_KEY = "STUDENT_POSITION"

class AddAndCreateStudentActivity : AppCompatActivity() {

    lateinit var nameTextView: EditText
    lateinit var classTextView: EditText
    private var studentPosition = POSITON_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_create_student)

        nameTextView = findViewById(R.id.nameEditTaxt)
        classTextView = findViewById(R.id.classEditText)


        val saveButton = findViewById<Button>(R.id.saveButton)

        studentPosition = intent.getIntExtra(STUDENT_POSITION_KEY, studentPosition)


        if(studentPosition != POSITON_NOT_SET) {
            saveButton.setOnClickListener{ view ->
                editStudent(studentPosition)
            }
            displayStudent(studentPosition)
        } else {
            saveButton.setOnClickListener{ view ->
                addNewStudent()
            }
        }
    }

    fun displayStudent(position: Int) {
        val student = DataManager.students[position]
        nameTextView.setText(student.name)
        classTextView.setText(student.className)
    }

    fun editStudent(position: Int) {
        DataManager.students[position].name = nameTextView.text.toString()
        DataManager.students[position].className = classTextView.text.toString()
        finish()
    }

    fun addNewStudent() {
        val name = nameTextView.text.toString()
        val className = classTextView.text.toString()

        val student = Student(name, className)
        DataManager.students.add(student)
        finish()
    }
}

