package com.saileshkumarpandey.scholarverifycollege

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.saileshkumarpandey.scholarverifycollege.database.DatabaseInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

class VerifyDocFaculty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_doc_faculty)
        val home = findViewById<ImageView>(R.id.bottom_navigation_home)
        val acd= findViewById<ImageView>(R.id.bottom_navigation_academic)
        val chat= findViewById<ImageView>(R.id.bottom_navigation_chat)
        home.setOnClickListener {
            val i = Intent(this,FacultyHome::class.java)
            startActivity(i)
        }
        val llFDocumentContainer = findViewById<LinearLayout>(R.id.llFDocumentContainer)
        lifecycleScope.launch {
            val documentDao = DatabaseInstance.getDatabase(applicationContext).documentDao()
            val facultyDao = DatabaseInstance.getDatabase(applicationContext).facultyDao()
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val email = sharedPreferences.getString("email", null)
            val fid = facultyDao.getFacultyIdByEmail(email!!)
            val documents = withContext(Dispatchers.IO){
                documentDao.getDocumentsByFid(fid!!)
            }.filter { it.status == "Pending" }
            for(document in documents){
                val cardView = layoutInflater.inflate(R.layout.fac_item_card,llFDocumentContainer,false)
                val cardSubmittedBy = cardView.findViewById<TextView>(R.id.tvFDocumentSubmittedBy)
                val cardDocumentTitle = cardView.findViewById<TextView>(R.id.tvFDocumentName)
                val cardImage = cardView.findViewById<ImageView>(R.id.tvDocumentImage)
                val btnApprove = cardView.findViewById<TextView>(R.id.btnApprove)
                val btnReject = cardView.findViewById<TextView>(R.id.btnReject)
                cardSubmittedBy.text = document.status
                cardDocumentTitle.text = document.title
                val imageUri = document.imagePath.toUri()
                cardImage.setImageURI(imageUri)
                btnApprove.setOnClickListener {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            document.status = "Approved"
                            documentDao.update(document)
                        }
                        cardSubmittedBy.text = "Approved"
                        btnApprove.isEnabled = false
                        btnReject.isEnabled = false
                        val faculty = facultyDao.getFacultyById(document.fid)
                        if(faculty!=null){
                            faculty.approved_count += 1
                            faculty.new_request_count -= 1
                            withContext(Dispatchers.IO) {
                                facultyDao.update(faculty)
                            }
                        }
                        val studentDao = DatabaseInstance.getDatabase(applicationContext).studentDao()
                        val student = studentDao.getStudentById(document.sid)
                        if(student!=null){
                            student.approved_count += 1
                            student.pending_count -= 1
                            withContext(Dispatchers.IO) {
                                studentDao.update(student)
                            }
                        }
                    }
                }
                btnReject.setOnClickListener {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            document.status = "Rejected"
                            documentDao.update(document)
                        }
                        cardSubmittedBy.text = "Rejected"
                        btnApprove.isEnabled = false
                        btnReject.isEnabled = false
                        val faculty = facultyDao.getFacultyById(document.fid)
                        if(faculty!=null){
                            faculty.rejected_count+= 1
                            faculty.new_request_count -= 1
                            withContext(Dispatchers.IO) {
                                facultyDao.update(faculty)
                            }
                        }
                        val studentDao = DatabaseInstance.getDatabase(applicationContext).studentDao()
                        val student = studentDao.getStudentById(document.sid)
                        if(student!=null){
                            student.rejected_count += 1
                            student.pending_count -= 1
                            withContext(Dispatchers.IO) {
                                studentDao.update(student)
                            }
                        }
                    }
                }
                llFDocumentContainer.addView(cardView)
            }
        }
    }
}