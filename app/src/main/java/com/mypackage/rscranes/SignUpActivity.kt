package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mypackage.rscranes.Models.Users
import com.mypackage.rscranes.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        if (auth.currentUser != null) {
            // User is already logged in, redirect to MainActivity
            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.login.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LogInActivity::class.java)
            startActivity(intent)
        }
        binding.Adminlogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginAsAdmin::class.java)
            startActivity(intent)
        }
        binding.Adminsignup.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val phone = binding.phone.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful, save user data to the database
                        val userId = auth.currentUser?.uid
                        val newUser = Users(email,phone)
                        userId?.let {
                            db.reference.child("Admins").child(it).setValue(newUser)
                        }
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpActivity, LoginAsAdmin::class.java)
                        startActivity(intent)
                    } else {
                        // Registration failed, display error message
                        Toast.makeText(this@SignUpActivity, "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }

        binding.Usersignup.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val phone = binding.phone.text.toString()

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful, save user data to the database
                        val userId = auth.currentUser?.uid
                        val newUser = Users(email,phone)
                        userId?.let {
                            db.reference.child("Users").child(it).setValue(newUser)
                        }
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity,MainActivity::class.java))
                    } else {
                        // Registration failed, display error message
                        Toast.makeText(this@SignUpActivity, "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
