package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mypackage.rscranes.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        auth = FirebaseAuth.getInstance()


        binding.logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }

        var craneList = ArrayList<dataModel>()
        var adapter = CraneAdapter(this,craneList)
    }
}