package com.mypackage.rscranes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the email and OTP from the intent
        val email = intent.getStringExtra("email")
        val otp = intent.getIntExtra("otp", -1)

        // Display the OTP to the user and get their input

        // Verify the OTP
//        verifyOtp(email!!, otp)

        // Finish the activity
        finish()
    }
}