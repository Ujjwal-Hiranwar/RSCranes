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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.databinding.ActivityAdminAddCraneBinding

class AdminAddCrane : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddCraneBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private lateinit var databaseReference: DatabaseReference
    private val adminUidList = ArrayList<String>()
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_add_crane)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        databaseReference = db.reference.child("Admins")
        val currentUser = auth.currentUser?.uid


        val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                imageUri = result.data?.data
                binding.uploadImg.setImageURI(imageUri)
            }
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

            if (imageUri != null) {
                uploadImageToFirebase(model, location, capacity, boomLength, flyjib, status,type)
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }

            checkUser(currentUser.toString())

            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun uploadImageToFirebase(
        model: String,
        location: String,
        capacity: String,
        boomLength: String,
        flyjib: String,
        status: String,
        type : String
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
                            status,
                            imageUrl,
                            type
                        )
                        val dataModel = dataModel(model,imageUrl)
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

    private fun checkUser(currentUser:String){
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    adminUidList.clear() // Clear the list before adding new data

                    for (snapshot in snapshot.children) {
                        val adminUid = snapshot.key // Get the UID of the admin
                        adminUid?.let { adminUidList.add(it) } // Add only non-null UID
                    }

                    val isAdmin = adminUidList.contains(currentUser)
                    Toast.makeText(this@AdminAddCrane, isAdmin.toString(), Toast.LENGTH_SHORT).show()

                    if (isAdmin){
                        startActivity(Intent(this@AdminAddCrane,AdminHomeActivity::class.java))
                    }
                    else{
                        Toast.makeText(
                            this@AdminAddCrane,
                            "Something went wrong in AdminAddCrane",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation of the database operation
                Toast.makeText(
                    this@AdminAddCrane,
                    "Failed to read admin UIDs: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

