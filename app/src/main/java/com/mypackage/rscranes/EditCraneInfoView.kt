package com.mypackage.rscranes

import Models.CraneDetails
import Models.dataModel
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.databinding.ActivityEditCraneInfoViewBinding

class EditCraneInfoView : AppCompatActivity() {
    private lateinit var binding :ActivityEditCraneInfoViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferenceDel: DatabaseReference
private lateinit var receivedValue:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_crane_info_view)
        val receivedValue = intent.getStringExtra("model_name_key").toString()
        Log.d("todo", receivedValue)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Crane details").child(receivedValue.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var i = 0
                    for (snapshot in snapshot.children) {
                        val crane = snapshot.getValue()
                        Log.d("todo", crane.toString())
                        when (i) {
                            0 -> {
                                binding.editcraneboomlength.setText(crane.toString())
                                i++
                            }
                            1 -> {
                                binding.editcranecapacity.setText(crane.toString())
                                i++
                            }
                            2 -> {
                                binding.des.setText(crane.toString())
                                i++
                            }
                            3 -> {
                                binding.editcraneflyjib.setText(crane.toString())
                                i++
                            }
                            4 -> {
                    //                          Image section
                                i++
                            }
                            5 -> {
                                binding.editcranelocation.setText(crane.toString())
                                i++
                            }
                            6 -> {
                                binding.editcranemodel.setText(crane.toString())
                                i++
                            }
                            7 -> {
                                binding.editstatus.setText(crane.toString())
                                i++
                            }
                            8 -> {
                                binding.editType.setText(crane.toString())
                                i++
                            }
                            else -> {
                            }
                        }


                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })



        binding.addcrane.setOnClickListener {

            val model = binding.editcranemodel.text.trim().toString()
            val location = binding.editcranelocation.text.trim().toString()
            val capacity = binding.editcranecapacity.text.trim().toString()
            val boomLength = binding.editcraneboomlength.text.trim().toString()
            val flyjib = binding.editcraneflyjib.text.trim().toString()
            val status = binding.editstatus.text.trim().toString()
            val type = binding.editType.text.trim().toString()
            val description = binding.des.text.trim().toString()

            uploadImageToFirebase(model,location,capacity,boomLength,flyjib,status,type,description)

            if (model.isNotEmpty() && location.isNotEmpty() && capacity.isNotEmpty() && boomLength.isNotEmpty() && flyjib.isNotEmpty() && status.isNotEmpty() && type.isNotEmpty()) {

//                checkUser(currentUser.toString())

                Toast.makeText(
                    this@EditCraneInfoView,
                    SplashScreenActivity.adminUser.isAdmin.toString(),
                    Toast.LENGTH_SHORT
                )
                    .show()

                if (SplashScreenActivity.adminUser.isAdmin) {
                    startActivity(Intent(this@EditCraneInfoView, AdminHomeActivity::class.java))
                } else {
                    Toast.makeText(
                        this@EditCraneInfoView,
                        "Something went wrong in AdminAddCrane",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(this, "Please fill all required details", Toast.LENGTH_SHORT).show()
            }

        }

    }
    fun uploadImageToFirebase(
        model: String,
        location: String,
        capacity: String,
        boomLength: String,
        flyjib: String,
        status: String,
        type: String,
        description: String
    ) {

        // Image upload successful


        val craneDetails = CraneDetails(
            model,
            location,
            capacity,
            boomLength,
            flyjib,
//            binding.uploadImg.toString(),
            status,
            type,
            description
        )
        val dataModel = dataModel(model,  binding.uploadImg.toString(), description)
        db.getReference("Crane details").child(receivedValue).setValue(craneDetails)
        db.getReference("Model and Image").child(model).setValue(dataModel)
        Toast.makeText(this, "Crane details added with image", Toast.LENGTH_SHORT)
            .show()
    }

}


