package com.mypackage.rscranes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mypackage.rscranes.SplashScreenActivity.adminUser.isAdmin
import com.mypackage.rscranes.databinding.ActivityChangePasswordBinding

class ChangePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var ref: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        binding.back.setOnClickListener {
            finish()
        }
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser!!.uid

        if(isAdmin){
            ref = db.reference.child("Admins").child(user).child(("email"))
        }
        else{
            ref = db.reference.child("Users").child(user).child(("email"))
        }
        ref!!.get().addOnSuccessListener {
            val email = it.value.toString()
            binding.send.setOnClickListener {
                resetpassword(email)
            }
        }

    }
    private fun resetpassword(useremail: String) {
        val email = binding.username.text.trim().toString()
        if (email.isNotEmpty() && email == useremail){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task->
                    if (task.isSuccessful){
                        Toast.makeText(this@ChangePassword, "Reset Password Link has been sent to you email", Toast.LENGTH_LONG).show()
                        finish()

                    }else{
                        Toast.makeText(this@ChangePassword, "Something went wrong !", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(this@ChangePassword, "Enter a valid email address", Toast.LENGTH_LONG).show()
        }
    }
}