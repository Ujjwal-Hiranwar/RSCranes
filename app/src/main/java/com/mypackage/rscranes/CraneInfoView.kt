package com.mypackage.rscranes

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.SplashScreenActivity.adminUser.isAdmin
import com.mypackage.rscranes.databinding.ActivityAdminAddCraneBinding
import com.mypackage.rscranes.databinding.ActivityCraneInfoViewBinding

class CraneInfoView : AppCompatActivity() {
    private lateinit var binding: ActivityCraneInfoViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crane_info_view)

        val receivedValue = intent.getStringExtra("key")
        Log.d("todo", receivedValue.toString())
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Crane details").child(receivedValue.toString())

        if (isAdmin) {
            binding.edit.visibility = View.VISIBLE
            binding.editImg.visibility = View.VISIBLE
        }
        binding.edit.setOnClickListener {
          val intent = Intent(this,Edit_Crane_Info::class.java)
            intent.putExtra("model_name_key",binding.cranemodelname.toString())
            startActivity(intent)
        }
        binding.back.setOnClickListener {
            finish()
        }


        binding.sellOrRentbtn.setOnClickListener {

            if (!isAdmin) {

                if (binding.sellOrRentbtn.text.toString().lowercase() == "rent") {
                    startActivity(Intent(this@CraneInfoView, CraneforRent::class.java))
                } else if (binding.sellOrRentbtn.text.toString().lowercase() == "sell") {
                    startActivity(Intent(this@CraneInfoView, CraneForSale::class.java))
                }
            }
        }
    }

}
