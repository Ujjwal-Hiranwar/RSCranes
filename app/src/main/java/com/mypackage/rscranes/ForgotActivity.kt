package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mypackage.rscranes.databinding.ActivityForgotBinding

class ForgotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signup.setOnClickListener {
            val intent = Intent(this@ForgotActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}