package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import Models.Users
import com.mypackage.rscranes.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val adminUidList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Admins")

        // Check if the user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserRole(currentUser)
        } else {
            // If not signed in, proceed with the sign-up flow
            setupSignUpFlow()
        }
    }

    private fun setupSignUpFlow() {
        binding.login.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LogInActivity::class.java)
            startActivity(intent)
        }

        binding.Adminlogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginAsAdmin::class.java)
            startActivity(intent)
        }

        binding.Adminsignup.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val phone = binding.phone.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful, send email verification link
                        val user = auth.currentUser
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener(this) { sendVerificationTask ->
                                if (sendVerificationTask.isSuccessful) {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Verification email sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@SignUpActivity,LoginAsAdmin::class.java))

                                } else {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Failed to send verification email: ${sendVerificationTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        // Save user data to the database
                        val userId = auth.currentUser?.uid
                        val newUser = Users(email, phone)
                        userId?.let {
                            db.reference.child("Admins").child(it).setValue(newUser)
                        }
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration failed, display error message
                        Toast.makeText(
                            this@SignUpActivity, "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        binding.Usersignup.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val phone = binding.phone.text.toString()

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful, send email verification link
                        val user = auth.currentUser
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener(this) { sendVerificationTask ->
                                if (sendVerificationTask.isSuccessful) {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Verification email sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@SignUpActivity,LogInActivity::class.java))
                                } else {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Failed to send verification email: ${sendVerificationTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        // Save user data to the database
                        val userId = auth.currentUser?.uid
                        val newUser = Users(email, phone)
                        userId?.let {
                            db.reference.child("Users").child(it).setValue(newUser)
                        }
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration failed, display error message
                        Toast.makeText(
                            this@SignUpActivity, "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun checkUserRole(currentUser: FirebaseUser) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    adminUidList.clear() // Clear the list before adding new data

                    for (snapshot in snapshot.children) {
                        val adminUid = snapshot.key // Get the UID of the admin
                        adminUid?.let { adminUidList.add(it) } // Add only non-null UID
                    }

                    if (adminUidList.contains(
                            currentUser.uid
                        ) && currentUser.isEmailVerified
                    ) {
                        // Current user is an admin and email is verified, open AdminHomeActivity
                        val intent = Intent(this@SignUpActivity, AdminHomeActivity::class.java)
                        startActivity(intent)
                    } else if (currentUser.isEmailVerified) {
                        // Current user is not an admin but email is verified, open MainActivity
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Email is not verified, display error message
                        Toast.makeText(
                            this@SignUpActivity,
                            "Please verify your email address",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation of the database operation
                Toast.makeText(
                    this@SignUpActivity,
                    "Failed to read admin UIDs: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}