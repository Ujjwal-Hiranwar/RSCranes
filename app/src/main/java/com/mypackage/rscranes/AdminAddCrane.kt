package com.mypackage.rscranes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mypackage.rscranes.Models.CraneDetails
import com.mypackage.rscranes.Models.dataModel
import com.mypackage.rscranes.databinding.ActivityAdminAddCraneBinding

class AdminAddCrane : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddCraneBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private lateinit var intent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_add_crane)

        db = FirebaseDatabase.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

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
                .crop()  // Enable cropping (optional)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(requestCode)

        }

        binding.addcrane.setOnClickListener {
            val model = binding.editcranemodel.text.toString()
            val location = binding.editcranelocation.text.toString()
            val capacity = binding.editcranecapacity.text.toString()
            val boomLength = binding.editcraneboomlength.text.toString()
            val flyjib = binding.editcraneflyjib.text.toString()
            val status = binding.editstatus.text.toString()

            if (imageUri != null) {
                uploadImageToFirebase(model, location, capacity, boomLength, flyjib, status)
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }

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
        status: String
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
                            imageUrl
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

}

