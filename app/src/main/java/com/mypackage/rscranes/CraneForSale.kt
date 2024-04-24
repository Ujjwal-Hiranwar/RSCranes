package com.mypackage.rscranes

import Models.RentRequests
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.SplashScreenActivity.adminUser.isAdmin
import com.mypackage.rscranes.databinding.ActivityCraneForSaleBinding

class CraneForSale : AppCompatActivity() {
    private lateinit var binding: ActivityCraneForSaleBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val adminUidList = ArrayList<String>()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crane_for_sale)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Sell Request")

        binding.back.setOnClickListener {
            finish()
        }
        binding.rentRequest.setOnClickListener {
            val capacity = binding.cranecapacity.text.trim().toString()
            val name = binding.name.text.trim().toString()
            val number = binding.telephone.text.trim().toString()
            val duration = binding.duration.text.trim().toString()
            val other = binding.otherDetails.text.trim().toString()

            if (capacity.isNotEmpty() && name.isNotEmpty() && number.isNotEmpty() && duration.isNotEmpty()) {
                val RentRequests = RentRequests(capacity,name,number,duration,other)
                databaseReference.child(name).setValue(RentRequests).addOnCompleteListener {
                    Toast.makeText(this, "Request Sent Successfully", Toast.LENGTH_SHORT).show()
//                    checkUser(currentUser!!)

//                    SplashScreenActivity.adminUser.checkAdmin(isAdmin)
                    Toast.makeText(this@CraneForSale, "$isAdmin", Toast.LENGTH_SHORT).show()
                    if (isAdmin) {
                        startActivity(Intent(this@CraneForSale, AdminHomeActivity::class.java))
                    }
                    else {
                        Toast.makeText(
                            this@CraneForSale,
                            "Something went wrong in CraneforRent",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@CraneForSale, MainActivity::class.java))

                    }
                }

            } else {
                Toast.makeText(
                    this@CraneForSale,
                    "Please fill all required details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun checkUser(currentUser: String) {
        databaseReference = db.reference.child("Admins")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    adminUidList.clear() // Clear the list before adding new data

                    for (snapshot in snapshot.children) {
                        val adminUid = snapshot.key // Get the UID of the admin
                        adminUid?.let { adminUidList.add(it)
                            Log.d(ContentValues.TAG,it)} // Add only non-null UID
                    }

                    val isAdmin = adminUidList.contains(currentUser)
                    Toast.makeText(this@CraneForSale, isAdmin.toString(), Toast.LENGTH_SHORT)
                        .show()

                    if (isAdmin) {
                        startActivity(Intent(this@CraneForSale, AdminHomeActivity::class.java))
                    }
                    else {
                        Toast.makeText(
                            this@CraneForSale,
                            "Something went wrong in CraneforRent",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@CraneForSale, MainActivity::class.java))

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation of the database operation
                Toast.makeText(
                    this@CraneForSale,
                    "Failed to read admin UIDs: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}