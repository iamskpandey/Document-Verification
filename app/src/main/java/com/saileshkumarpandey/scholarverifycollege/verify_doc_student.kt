package com.saileshkumarpandey.scholarverifycollege

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.saileshkumarpandey.scholarverifycollege.database.DatabaseInstance
import com.saileshkumarpandey.scholarverifycollege.database.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class verify_doc_student : AppCompatActivity() {
    private lateinit var ivSelectedImage: ImageView
    private lateinit var tvImagePath: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_doc_student)
        val home = findViewById<ImageView>(R.id.imghome)
        val acd= findViewById<ImageView>(R.id.imgacd)
        val chat= findViewById<ImageView>(R.id.imgchat)
        ivSelectedImage = findViewById(R.id.ivSelectedImage)
        tvImagePath = findViewById(R.id.tvImagePath)
        val btnSelectImage = findViewById<Button>(R.id.btnSelectImage)
        val btnUploadForVerification = findViewById<Button>(R.id.btnUploadForVerification)
        val etDocumentTitle = findViewById<EditText>(R.id.etDocumentTitle)
        showSubmittedDocuments()
        btnSelectImage.setOnClickListener{
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i,1001)
        }

        btnUploadForVerification.setOnClickListener {
            val documentTitle = etDocumentTitle.text.toString()
            val imagePath = tvImagePath.text.toString().removePrefix("Image Path: ").trim()

            if (documentTitle.isEmpty() || imagePath.isEmpty()) {
                Toast.makeText(this, "Please provide a title and select an image.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val documentDao = DatabaseInstance.getDatabase(applicationContext).documentDao()
                val facultyId = withContext(Dispatchers.IO) {
                    documentDao.getFacultyWithLeastRequests()
                }

                val sharedp = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val studentEmail = sharedp.getString("email", null)
                val studentDao = DatabaseInstance.getDatabase(applicationContext).studentDao()
                val studentId = withContext(Dispatchers.IO) {
                    studentDao.getStudentIdByEmail(studentEmail!!)
                }
                val document = Document(
                    title = documentTitle,
                    imagePath = imagePath,
                    status = "Pending",
                    sid = studentId!!,
                    fid = facultyId
                )
                val facultyDao = DatabaseInstance.getDatabase(applicationContext).facultyDao()
                val faculty = withContext(Dispatchers.IO) {
                    facultyDao.getFacultyById(facultyId)
                }
                if(faculty!=null){
                    faculty.new_request_count += 1
                    withContext(Dispatchers.IO) {
                        facultyDao.update(faculty)
                    }
                }
                val student = withContext(Dispatchers.IO) {
                    studentDao.getStudentById(studentId)
                }
                if(student!=null){
                    student.pending_count += 1
                    withContext(Dispatchers.IO) {
                        studentDao.update(student)
                    }
                }
                withContext(Dispatchers.IO) {
                    documentDao.insert(document)
                }
                Toast.makeText(this@verify_doc_student, "Document uploaded successfully!", Toast.LENGTH_SHORT).show()
                showSubmittedDocuments()
            }
            tvImagePath.text = "Image Path: "
            ivSelectedImage.setImageResource(0)
            etDocumentTitle.text.clear()
        }
        home.setOnClickListener {
            val i = Intent(this,StudentHome::class.java)
            startActivity(i)
        }
        chat.setOnClickListener {
            val i = Intent(this, NotificationView::class.java)
            startActivity(i)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val imageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            val imagePath = saveImageToInternalStorage(bitmap)
            if (imagePath != null) {
                ivSelectedImage.setImageBitmap(bitmap)
                tvImagePath.text = "Image Path: $imagePath"
            } else {
                Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        val filename = "image_${System.currentTimeMillis()}.png"
        val file = File(filesDir, filename)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    private fun showSubmittedDocuments(){
        val containerCards = findViewById<LinearLayout>(R.id.containerCards)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val studentEmail = sharedPreferences.getString("email", null)
        lifecycleScope.launch {
            val documentDao = DatabaseInstance.getDatabase(applicationContext).documentDao()
            val studentDao = DatabaseInstance.getDatabase(applicationContext).studentDao()
            val studentId = withContext(Dispatchers.IO) {
                studentDao.getStudentIdByEmail(studentEmail!!)
            }
            val documents = withContext(Dispatchers.IO) {
                documentDao.getDocumentsBySid(studentId!!)
            }
            containerCards.removeAllViews()
            for (document in documents) {
                val cardView = layoutInflater.inflate(R.layout.item_card, containerCards, false)
                val titleTextView = cardView.findViewById<TextView>(R.id.tvDocumentTitle)
                val statusTextView = cardView.findViewById<TextView>(R.id.tvDocumentStatus)
                titleTextView.text = document.title
                statusTextView.text = document.status
                containerCards.addView(cardView)
            }
        }
    }
}
