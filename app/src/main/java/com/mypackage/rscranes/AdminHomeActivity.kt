package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.mypackage.rscranes.Adapters.CraneAdapter
import com.mypackage.rscranes.Models.dataModel
import com.mypackage.rscranes.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_home)
        auth = FirebaseAuth.getInstance()
        binding.logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.addCranes.setOnClickListener {
            startActivity(Intent(this@AdminHomeActivity,AdminAddCrane::class.java))
        }

        var craneList = ArrayList<dataModel>()
        var adapter = CraneAdapter(this,craneList)


    }
}