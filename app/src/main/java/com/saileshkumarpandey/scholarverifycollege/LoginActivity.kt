package com.saileshkumarpandey.scholarverifycollege

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saileshkumarpandey.scholarverifycollege.database.DatabaseInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_login)

        val signup = findViewById<TextView>(R.id.sign_up)
        val login = findViewById<TextView>(R.id.login_button)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val roleSpinner = findViewById<Spinner>(R.id.role_spinner)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        val savedRole = sharedPreferences.getString("role", null)
        if (savedEmail != null && savedPassword != null && savedRole != null) {
            username.setText(savedEmail)
            password.setText(savedPassword)
            roleSpinner.setSelection(if (savedRole == "Student") 0 else 1)
        }

        signup.setOnClickListener {
            val i = Intent(this, SignUpPage::class.java)
            startActivity(i)
        }

        login.setOnClickListener {
            val email = username.text.toString()
            val pass = password.text.toString()
            val role = roleSpinner.selectedItem.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = DatabaseInstance.getDatabase(applicationContext)
                when (role) {
                    "Student" -> {
                        val student = db.studentDao().getStudentByEmailAndPassword(email, pass)
                        runOnUiThread {
                            if (student != null) {
                                val intent = Intent(this@LoginActivity, StudentHome::class.java)
                                val editor = sharedPreferences.edit()
                                editor.putString("email", email)
                                editor.putString("name", student.name)
                                editor.putString("role", role)
                                editor.putString("password", pass)
                                editor.apply()
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    "Faculty" -> {
                        val faculty = db.facultyDao().getFacultyByEmailAndPassword(email, pass)
                        runOnUiThread {
                            if (faculty != null) {
                                val intent = Intent(this@LoginActivity, FacultyHome::class.java)
                                val editor = sharedPreferences.edit()
                                editor.putString("email", email)
                                editor.putString("name", faculty.name)
                                editor.putString("role", role)
                                editor.putString("password", pass)
                                editor.apply()
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Invalid role selected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
