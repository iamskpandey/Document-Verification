package com.saileshkumarpandey.scholarverifycollege

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.saileshkumarpandey.scholarverifycollege.database.DatabaseInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_view)
        val home = findViewById<ImageView>(R.id.imghome)
        val acd= findViewById<ImageView>(R.id.imgacd)
        val chat= findViewById<ImageView>(R.id.imgchat)

        val cont = findViewById<LinearLayout>(R.id.notificationCardContainer)
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        val savedRole = sharedPreferences.getString("role", null)
        acd.setOnClickListener {
            if(savedRole== "Student"){
                val i = Intent(this, verify_doc_student::class.java)
                startActivity(i)
            }
            else if(savedRole == "Faculty"){
                val i = Intent(this,VerifyDocFaculty::class.java)
                startActivity(i)
            }
        }
        home.setOnClickListener {
            if(savedRole== "Student"){
                val i = Intent(this, StudentHome::class.java)
                startActivity(i)
            }
            else if(savedRole == "Faculty"){
                val i = Intent(this,FacultyHome::class.java)
                startActivity(i)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            if(savedRole == "Faculty"){
                val fid= savedEmail?.let {
                    DatabaseInstance.getDatabase(applicationContext).facultyDao().getFacultyIdByEmail(it)
                }
                if (fid!= null) {
                    val documentDao = DatabaseInstance.getDatabase(applicationContext).documentDao()
                    val documents = withContext(Dispatchers.IO){
                        documentDao.getDocumentsByFid(fid)
                    }.filter { it.status == "Pending" }
                    for(document in documents){
                        val noticard = layoutInflater.inflate(R.layout.noti_card,cont,false)
                        val notimsg = noticard.findViewById<TextView>(R.id.notimsg)
                        val name = noticard.findViewById<TextView>(R.id.name)
                        val student = DatabaseInstance.getDatabase(applicationContext).studentDao().getStudentById(document.sid)
                        notimsg.text = "Dear Sir, Please review my document: ${document.title}"
                        name.text = student?.name
                        withContext(Dispatchers.Main){
                            cont.addView(noticard, 0)
                        }
                    }
                }
            }
            else if(savedRole == "Student"){
                val studentId = savedEmail?.let {
                    DatabaseInstance.getDatabase(applicationContext).studentDao().getStudentIdByEmail(it)
                }
                if (studentId != null) {
                    val documentDao = DatabaseInstance.getDatabase(applicationContext).documentDao()
                    val documents = withContext(Dispatchers.IO){
                        documentDao.getDocumentsBySid(studentId)
                    }.filter { it.status == "Approved" || it.status == "Rejected" }
                    for(document in documents){
                        val noticard = layoutInflater.inflate(R.layout.noti_card,cont,false)
                        val notimsg = noticard.findViewById<TextView>(R.id.notimsg)
                        val name = noticard.findViewById<TextView>(R.id.name)
                        val faculty = DatabaseInstance.getDatabase(applicationContext).facultyDao().getFacultyById(document.fid)
                        notimsg.text = "Dear Student, Your document: ${document.title} has been ${document.status}"
                        name.text = faculty?.name
                        withContext(Dispatchers.Main){
                            cont.addView(noticard, 0)
                        }
                    }
                }
            }
        }
    }
}