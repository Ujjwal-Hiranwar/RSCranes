package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mypackage.rscranes.databinding.ActivityLoginAsAdminBinding

class LoginAsAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityLoginAsAdminBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_as_admin)
        auth = FirebaseAuth.getInstance()

        binding.adminlogin.setOnClickListener {
            val email = binding.adminusername.text.toString()
            val password = binding.adminpassword.text.toString()
            val uniqueId = binding.adminuniqeid.text.toString().trim()


            if (email.isNotEmpty() && password.isNotEmpty() && uniqueId.isNotEmpty() && uniqueId=="RS CRANES") {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }        }
    }
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User logged in successfully
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginAsAdmin, AdminHomeActivity::class.java)
                    startActivity(intent)
                } else {
                    // Check if the user does not exist
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, "Account does not exist. Please create an account first.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Other errors occurred during login
                        Toast.makeText(this, "Login failed. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}