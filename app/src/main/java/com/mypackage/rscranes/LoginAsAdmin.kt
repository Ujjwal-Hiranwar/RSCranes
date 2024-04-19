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

            if (email.isNotEmpty() && password.isNotEmpty() && uniqueId.isNotEmpty() && uniqueId == "RS CRANES") {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.userlogin.setOnClickListener {
            val intent = Intent(this@LoginAsAdmin, LogInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Check if the user or admin is verified
                    val currentUser = auth.currentUser
                    if (currentUser != null && !currentUser.isEmailVerified) {
                        // User or admin is not verified, display error message
                        Toast.makeText(this, "Please verify your email address", Toast.LENGTH_SHORT).show()
                        currentUser.sendEmailVerification()
                            .addOnCompleteListener(this) { sendVerificationTask ->
                                if (sendVerificationTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Verification email sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to send verification email: ${sendVerificationTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        // User or admin is verified, open AdminHomeActivity
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginAsAdmin, AdminHomeActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    // Check if the user does not exist
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        Toast.makeText(
                            this,
                            "Account does not exist. Please create an account first.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Other errors occurred during login
                        Toast.makeText(
                            this,
                            "Login failed. Please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}