package com.saileshkumarpandey.scholarverifycollege

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.saileshkumarpandey.scholarverifycollege.database.DatabaseInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StudentHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_home)
        val home = findViewById<ImageView>(R.id.imghome)
        val acd= findViewById<ImageView>(R.id.imgacd)
        val chat= findViewById<ImageView>(R.id.imgchat)
        acd.setOnClickListener {
            val i = Intent(this,verify_doc_student::class.java)
            startActivity(i)
        }
        chat.setOnClickListener {
            val i = Intent(this, NotificationView::class.java)
            startActivity(i)
        }

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val studentEmail = sharedPreferences.getString("email", null)
        val studentName = sharedPreferences.getString("name", null)
        val tvStudentName = findViewById<android.widget.TextView>(R.id.tvStudentName)
        tvStudentName.text = "Hi, $studentName"

         GlobalScope.launch(Dispatchers.IO) {
             val db = DatabaseInstance.getDatabase(applicationContext)
            val students = db.studentDao().getAllStudents()
            val student = students.find { it.email == studentEmail }
            student?.let {
                launch(Dispatchers.Main) {
                    val tvScheduled = findViewById<android.widget.TextView>(R.id.tvScheduledValue)
                    val tvApproved = findViewById<android.widget.TextView>(R.id.tvApprovedValue)
                    val tvRejected = findViewById<android.widget.TextView>(R.id.tvRejectedValue)
                    val tvPending = findViewById<android.widget.TextView>(R.id.tvPendingValue)
                    val cgpa = findViewById<android.widget.TextView>(R.id.tvCgpaValue)
                    val noOfCourseStudied = findViewById<android.widget.TextView>(R.id.tvCoursesStudiedValue)
                    val noOfBacklog = findViewById<android.widget.TextView>(R.id.tvBacklogValue)
                    val noOfCourseRemained = findViewById<android.widget.TextView>(R.id.tvCoursesRemainingValue)

                    tvScheduled.text = it.scheduled_count.toString()
                    tvApproved.text = it.approved_count.toString()
                    tvRejected.text = it.rejected_count.toString()
                    tvPending.text = it.pending_count.toString()
                    cgpa.text = it.cgpa.toString()
                    noOfCourseStudied.text = it.no_of_courses_studied.toString()
                    noOfBacklog.text = it.backlog.toString()
                    noOfCourseRemained.text = it.number_of_remained.toString()
                }
            }
         }
    }
}