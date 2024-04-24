package com.mypackage.rscranes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import Models.CraneDetails
import Models.dataModel
import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.NullValue
import com.mypackage.rscranes.SplashScreenActivity.adminUser.isAdmin
import com.mypackage.rscranes.databinding.ActivityAdminAddCraneBinding

class AdminAddCrane : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddCraneBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_add_crane)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        databaseReference = db.reference.child("Admins")

        val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                imageUri = result.data?.data
                binding.uploadImg.setImageURI(imageUri)
            }
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.uploadImg.setOnClickListener {
            val requestCode =
                launcher.hashCode()
            ImagePicker.with(this@AdminAddCrane)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(requestCode)

        }


        binding.addcrane.setOnClickListener {
            val model = binding.editcranemodel.text.trim().toString()
            val location = binding.editcranelocation.text.trim().toString()
            val capacity = binding.editcranecapacity.text.trim().toString()
            val boomLength = binding.editcraneboomlength.text.trim().toString()
            val flyjib = binding.editcraneflyjib.text.trim().toString()
            val status = binding.editstatus.text.trim().toString()
            val type = binding.editType.text.trim().toString()
            val description = binding.des.text.trim().toString()

            if (model.isNotEmpty() && location.isNotEmpty() && capacity.isNotEmpty() && boomLength.isNotEmpty() && flyjib.isNotEmpty() && status.isNotEmpty() && type.isNotEmpty()) {

                if (imageUri != null) {
                    uploadImageToFirebase(
                        model,
                        location,
                        capacity,
                        boomLength,
                        flyjib,
                        status,
                        type,
                        description
                    )
                } else {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                }

//                checkUser(currentUser.toString())

                Toast.makeText(this@AdminAddCrane, isAdmin.toString(), Toast.LENGTH_SHORT)
                    .show()

                if (isAdmin) {
                    startActivity(Intent(this@AdminAddCrane, AdminHomeActivity::class.java))
                } else {
                    Toast.makeText(
                        this@AdminAddCrane,
                        "Something went wrong in AdminAddCrane",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }else {
                Toast.makeText(this, "Please fill all required details", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun uploadImageToFirebase(
        model: String,
        location: String,
        capacity: String,
        boomLength: String,
        flyjib: String,
        status: String,
        type: String,
        description: String
    ) {
        val imageRef =
            storageRef.child("crane_images/" + model + "_" + System.currentTimeMillis() + ".jpg") // Create unique filename with timestamp

        imageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    // Image upload successful
                    imageRef.downloadUrl.addOnCompleteListener { task ->
                        val imageUrl = task.result.toString()
                        val craneDetails = CraneDetails(
                            model,
                            location,
                            capacity,
                            boomLength,
                            flyjib,
                            imageUrl,
                            status,
                            type,
                            description
                        )
                        val dataModel = dataModel(model, imageUrl,description)
                        db.getReference("Crane details").child(model).setValue(craneDetails)
                        db.getReference("Model and Image").child(model).setValue(dataModel)
                        Toast.makeText(this, "Crane details added with image", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Image upload failed
                    Toast.makeText(
                        this,
                        "Image upload failed: " + exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.uploadImg.setImageURI(imageUri)  // Set the image in the ImageView
        }
    }

}

