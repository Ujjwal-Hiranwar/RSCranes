package com.mypackage.rscranes

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.SplashScreenActivity.adminUser.isAdmin
import com.mypackage.rscranes.databinding.ActivityCraneInfoViewBinding

class CraneInfoView : AppCompatActivity() {
    private lateinit var binding: ActivityCraneInfoViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crane_info_view)

        val receivedValue = intent.getStringExtra("key")
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Crane details").child(receivedValue.toString())
        if (isAdmin) {
            binding.edit.visibility = View.VISIBLE
            binding.editImg.visibility = View.VISIBLE
        }else{
            binding.edit.visibility = View.INVISIBLE
            binding.editImg.visibility = View.INVISIBLE
        }
        binding.edit.setOnClickListener {
          val intent = Intent(this,EditCraneInfoView::class.java)
            intent.putExtra("model_name_key",receivedValue.toString())
            startActivity(intent)
        }
        binding.back.setOnClickListener {
            finish()
        }

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var i = 0
                    for (snapshot in snapshot.children) {
                        val crane = snapshot.getValue()
                        Log.d("todo", crane.toString())
                        when (i) {
                            0 -> {
                                binding.craneboomlength.text = crane.toString()
                                i++
                            }
                            1 -> {
                                binding.cranecapacity.text = crane.toString()
                                i++
                            }
                            2 -> {
                                i++
                            }
                            3 -> {
                                binding.craneflyjib.text = crane.toString()
                                i++
                            }
                            4 -> {
                                i++
                            }
                            5 -> {
                                binding.cranelocation.text = crane.toString()
                                i++
                            }
                            6 -> {
                                binding.cranemodelname.text = crane.toString()
                                i++
                            }
                            7 -> {
                                binding.cranestatus.text = crane.toString()
                                i++
                            }
                            else -> {
                                binding.sellOrRentbtn.text = crane.toString()
                                i++
                            }
                        }


                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })


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
