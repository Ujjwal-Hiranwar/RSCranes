package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.mypackage.rscranes.databinding.ActivityForgotBinding

class ForgotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot)
        auth = FirebaseAuth.getInstance()

        binding.send.setOnClickListener {
            val email = binding.email.text.toString()

            if (email.isNotEmpty()) {
                auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val methods = task.result?.signInMethods

                            if (methods != null && methods.isNotEmpty()) {
                                // User exists, send OTP to their email address
                                sendOtpToEmail(email)
                            } else {
                                Toast.makeText(
                                    this,
                                    "User with this email does not exist.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to check if user exists. Please try again later.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signup.setOnClickListener {
            val intent = Intent(this@ForgotActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendOtpToEmail(email: String) {
        // Generate a random OTP
        val otp = (100000..999999).random()

        // Send the OTP to the user's email
        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("otp", otp)
        startActivity(intent)
    }

    private fun verifyOtp(email: String, otp: Int) {
        // Get the OTP from the user
        val userOtp = intent.getIntExtra("otp", -1)

        // Verify the OTP
        if (userOtp == otp) {
            Toast.makeText(this, "OTP verified", Toast.LENGTH_SHORT).show()

            // Add your logic here to allow the user to reset their password
        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
        }
    }
}