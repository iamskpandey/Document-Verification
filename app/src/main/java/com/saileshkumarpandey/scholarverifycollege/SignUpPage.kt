package com.saileshkumarpandey.scholarverifycollege

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saileshkumarpandey.scholarverifycollege.database.DatabaseInstance
import com.saileshkumarpandey.scholarverifycollege.database.Faculty
import com.saileshkumarpandey.scholarverifycollege.database.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)

        val nameField = findViewById<EditText>(R.id.name)
        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val confirmPasswordField = findViewById<EditText>(R.id.confirm_password)
        val roleSpinner = findViewById<Spinner>(R.id.role_spinner)
        val signUpButton = findViewById<Button>(R.id.signup_button)
        val login = findViewById<TextView>(R.id.loginscreen)

        login.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

        signUpButton.setOnClickListener {
            val name = nameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()
            val role = roleSpinner.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = DatabaseInstance.getDatabase(applicationContext)
                if (role == "Student") {
                    val student = Student(
                        name = name,
                        email = email,
                        password = password,
                        role = role,
                        cgpa = (5..10).random() + (0..99).random() / 100.0,
                        no_of_courses_studied = (1..30).random(),
                        backlog = (0..6).random(),
                        number_of_remained = (0..20).random(),
                        scheduled_count = 0,
                        approved_count = 0,
                        rejected_count = 0,
                        pending_count = 0
                    )
                    db.studentDao().insert(student)
                    runOnUiThread {
                        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("email", email)
                        editor.putString("name", name)
                        editor.putString("role", role)
                        editor.putString("password", password)
                        editor.apply()
                        val intent = Intent(this@SignUpPage, StudentHome::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    }
                } else if (role == "Faculty") {
                    val faculty = Faculty(
                        name = name,
                        email = email,
                        password = password,
                        role = role,
                        new_request_count = 0,
                        approved_count = 0,
                        rejected_count = 0,
                        pending_count = 0
                    )
                    db.facultyDao().insert(faculty)
                    runOnUiThread {
                        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("email", email)
                        editor.putString("name", name)
                        editor.putString("role", role)
                        editor.putString("password", password)
                        editor.apply()
                        val intent = Intent(this@SignUpPage, FacultyHome::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}
