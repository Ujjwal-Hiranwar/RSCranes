package com.mypackage.rscranes

import android.content.ContentValues
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
import com.mypackage.rscranes.databinding.ActivityRequestInfoviewBinding

class RequestInfoview : AppCompatActivity() {
    private lateinit var binding: ActivityRequestInfoviewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_infoview)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        val receivedValue = intent.getStringExtra("key")
        databaseReference = db.reference.child("Rent Request").child(receivedValue.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var i = 0;
                    for (snapshot in snapshot.children) {
                        val crane= snapshot.getValue()
                        Log.d("todo",crane.toString())
                        if(i==0){
                            binding.capacity.text = crane.toString()
                            i++
                        }
                        else if(i==1){
                            binding.duration.text = crane.toString()
                            i++
                        }
                        else if(i==2){
                            binding.craneName.text = crane.toString()
                            i++
                        }
                        else if(i==3){
                            binding.number.text = crane.toString()
                            i++
                        }
                        else if(i==4){
                            binding.details.text = crane.toString()
                            i++
                        }
                        else{
                            Toast.makeText(this@RequestInfoview, "Something went wrong in RequestInfoView", Toast.LENGTH_SHORT).show()
                        }


                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })


    }
}