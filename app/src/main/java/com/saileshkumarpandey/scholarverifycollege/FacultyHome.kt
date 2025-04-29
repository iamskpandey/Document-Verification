package com.saileshkumarpandey.scholarverifycollege

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.saileshkumarpandey.scholarverifycollege.database.DatabaseInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacultyHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_home)
        val home = findViewById<ImageView>(R.id.bottom_navigation_home)
        val acd= findViewById<ImageView>(R.id.bottom_navigation_academic)
        val chat= findViewById<ImageView>(R.id.bottom_navigation_chat)
        acd.setOnClickListener {
            val i = Intent(this,VerifyDocFaculty::class.java)
            startActivity(i)
        }
        chat.setOnClickListener {
            val i = Intent(this, NotificationView::class.java)
            startActivity(i)
        }

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val facultyEmail = sharedPreferences.getString("email", null)
        val facultyName = sharedPreferences.getString("name", null)

        val tvFacultyName = findViewById<TextView>(R.id.tvFacultyName)
        tvFacultyName.text = "Hi, $facultyName"

        GlobalScope.launch(Dispatchers.IO) {
            val db = DatabaseInstance.getDatabase(applicationContext)
            val faculties = db.facultyDao().getAllFaculty()
            val faculty = faculties.find { it.email == facultyEmail }
            faculty?.let {
                withContext(Dispatchers.Main) {
                    val tvNewRequest = findViewById<TextView>(R.id.tvScheduledValue)
                    val tvApproved = findViewById<TextView>(R.id.tvApprovedValue)
                    val tvRejected = findViewById<TextView>(R.id.tvRejectedValue)
                    val tvPending = findViewById<TextView>(R.id.tvPendingValue)

                    tvNewRequest.text = it.new_request_count.toString()
                    tvApproved.text = it.approved_count.toString()
                    tvRejected.text = it.rejected_count.toString()
                    tvPending.text = it.pending_count.toString()
                }
            }
        }
    }
}