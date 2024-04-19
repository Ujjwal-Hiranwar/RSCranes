package com.mypackage.rscranes

import Adapters.CraneAdapter
import Models.CraneDetails
import Models.dataModel
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.databinding.ActivityCraneInfoViewBinding
import com.mypackage.rscranes.databinding.ActivityMainBinding

class CraneInfoView : AppCompatActivity() {
    private lateinit var binding: ActivityCraneInfoViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crane_info_view)

        val receivedValue = intent.getStringExtra("key")
        Log.d("todo",receivedValue.toString())
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Crane details").child(receivedValue.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var i = 0;
                    for (snapshot in snapshot.children) {
                        val crane= snapshot.getValue()
                        Log.d("todo",crane.toString())
                        if(i==0){
                            binding.craneboomlength.text = crane.toString()
                            i++
                        }
                      else if(i==1){
                            binding.cranecapacity.text = crane.toString()
                            i++
                        }
                       else if(i==2){
                            binding.craneflyjib.text = crane.toString()
                            i++
                        }
                        else if(i==3){
                            i++
                        }
                        else if(i==4){
                            binding.cranelocation.text = crane.toString()
                            i++
                        }
                       else if(i==5){
                            binding.cranemodelname.text = crane.toString()
                            i++
                        }
                        else{
                            binding.cranestatus.text = crane.toString()
                            binding.sellOrRentbtn.text = crane.toString()
                            i++
                        }


                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })

        binding.sellOrRentbtn.setOnClickListener {

            if (binding.cranestatus.text.toString().lowercase() == "rent") {
                startActivity(Intent(this@CraneInfoView, CraneforRent::class.java))
            } else if (binding.cranestatus.text.toString().lowercase() == "sell") {
                startActivity(Intent(this@CraneInfoView,CraneForSale::class.java))
            }
        }
    }

}
