package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mypackage.rscranes.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signup.setOnClickListener {
            val intent = Intent(this@LogInActivity,SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.forgot.setOnClickListener {
            val intent = Intent(this@LogInActivity,ForgotActivity::class.java)
            startActivity(intent)
        }
    }
}