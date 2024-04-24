package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mypackage.rscranes.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)
        auth = FirebaseAuth.getInstance()

        binding.login.setOnClickListener {
            val email = binding.email.text.trim().toString()
            val password = binding.password.text.trim().toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signup.setOnClickListener {
            val intent = Intent(this@LogInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.Adminlogin.setOnClickListener {
            val intent = Intent(this@LogInActivity, LoginAsAdmin::class.java)
            startActivity(intent)
        }

        binding.forgot.setOnClickListener {
            resetpassword()
        }
    }

    private fun resetpassword() {
        val email = binding.email.text.trim().toString()
        if (email.isNotEmpty()){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task->
                    if (task.isSuccessful){
                        Toast.makeText(this@LogInActivity, "Reset Password Link has been sent to you email", Toast.LENGTH_LONG).show()

                    }else{
                        Toast.makeText(this@LogInActivity, "Something went wrong !", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(this@LogInActivity, "Please Enter email first then click on Forgot password", Toast.LENGTH_LONG).show()
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
                        // User or admin is verified, open MainActivity
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LogInActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
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