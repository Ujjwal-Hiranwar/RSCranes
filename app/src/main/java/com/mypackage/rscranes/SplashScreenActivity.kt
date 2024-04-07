package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val adminUidList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Admins")

        // Check if the user is already signed in
        val currentUser = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser != null) {
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            adminUidList.clear() // Clear the list before adding new data

                            for (snapshot in snapshot.children) {
                                val adminUid = snapshot.key // Get the UID of the admin
                                adminUid?.let { adminUidList.add(it) } // Add only non-null UID
                            }

                            if (adminUidList.contains(currentUser.uid)) {
                                // Current user is an admin, open AdminHomeActivity
                                val intent = Intent(this@SplashScreenActivity, AdminHomeActivity::class.java)
                                startActivity(intent)
                            } else {
                                // Current user is not an admin, open MainActivity
                                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle cancellation of the database operation
                        Toast.makeText(
                            this@SplashScreenActivity,
                            "Failed to read admin UIDs: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            finish()
        },3000)


    }

}