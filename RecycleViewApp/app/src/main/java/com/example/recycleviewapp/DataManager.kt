package com.example.recycleviewapp

object DataManager {
    val students = mutableListOf<Student>()

    init {
        createMockData()
    }

    fun createMockData() {
        var student = Student("Alessio", "MAP19")
        students.add(student)
        student = Student("Michael", "MAP19")
        students.add(student)
        student = Student("Kalle", "MAP19")
        students.add(student)
        student = Student("Lucifer", "MAP19")
        students.add(student)
        student = Student("Mareep", "Beep-Beep")
        students.add(student)
    }
}