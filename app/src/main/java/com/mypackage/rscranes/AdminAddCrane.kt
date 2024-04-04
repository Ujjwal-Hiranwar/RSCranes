package com.mypackage.rscranes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.FirebaseDatabase
import com.mypackage.rscranes.databinding.ActivityAdminAddCraneBinding

class AdminAddCrane : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAddCraneBinding
    private lateinit var db : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_admin_add_crane)
        db = FirebaseDatabase.getInstance()
        binding.addcrane.setOnClickListener {
            val model = binding.editcranemodel.text.toString()
            val location = binding.editcranelocation.text.toString()
            val capacity = binding.editcranecapacity.text.toString()
            val boomLength = binding.editcraneboomlength.text.toString()
            val flyjib = binding.editcraneflyjib.text.toString()
            val status = binding.editstatus.text.toString()
        db.getReference("Crane details").child(model)
            .setValue(CraneDetails(model,location,capacity,boomLength,flyjib,status))
            Toast.makeText(this, "Crane details Added", Toast.LENGTH_SHORT).show()
            
        }
    }
}