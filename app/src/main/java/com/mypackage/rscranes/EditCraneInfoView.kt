package com.mypackage.rscranes

import Models.CraneDetails
import Models.dataModel
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mypackage.rscranes.databinding.ActivityEditCraneInfoViewBinding
import com.squareup.picasso.Picasso

class EditCraneInfoView : AppCompatActivity() {
    private lateinit var binding :ActivityEditCraneInfoViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private var imagei: Uri? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var receivedValue:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_crane_info_view)
        receivedValue = intent.getStringExtra("model_name_key").toString()
        Log.d("todo", receivedValue)
        storageRef = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Crane details").child(receivedValue)



        binding.uploadImg.setOnClickListener {
            // Open image picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
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
                                imagei = Uri.parse(crane.toString())
                                Picasso.get().load(imagei).into(binding.uploadImg)
                                Log.d("checking When" ,imagei.toString())
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


        binding.save.setOnClickListener {

            val model = binding.editcranemodel.text.toString()
            val location = binding.editcranelocation.text.toString()
            val capacity = binding.editcranecapacity.text.toString()
            val boomLength = binding.editcraneboomlength.text.toString()
            val flyjib = binding.editcraneflyjib.text.toString()
            val status = binding.editstatus.text.toString()
            val type = binding.editType.text.toString()
            val description = binding.des.text.toString()

            uploadImageToFirebase(model,location,capacity,boomLength,flyjib,status,type,description)

            if (model.isNotEmpty() && location.isNotEmpty() && capacity.isNotEmpty() && boomLength.isNotEmpty() && flyjib.isNotEmpty() && status.isNotEmpty() && type.isNotEmpty()) {
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
        // Check if user has selected a new image
        val isNewImageSelected = imageUri != null

        if (isNewImageSelected) {
            // User selected a new image
            imageUri?.let { uri ->
                val imageRef = storageRef.child("crane_images/$receivedValue.jpg")
                imageRef.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image upload successful
                        imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                            // Update the image URL in the database
                            val imageUrlString = imageUrl.toString()
                            updateCraneDetailsWithImage(
                                model,
                                location,
                                capacity,
                                boomLength,
                                flyjib,
                                imageUrlString,
                                status,
                                type,
                                description
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Image upload failed
                        Toast.makeText(
                            this,
                            "Image upload failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        } else {
            // User didn't select a new image, update with previous image URL
            val previousImageUrl = imagei?.toString() ?: ""
            updateCraneDetailsWithImage(
                model,
                location,
                capacity,
                boomLength,
                flyjib,
                previousImageUrl,
                status,
                type,
                description
            )
        }
    }

    private fun updateCraneDetailsWithImage(
        model: String,
        location: String,
        capacity: String,
        boomLength: String,
        flyjib: String,
        imageUrl: String,
        status: String,
        type: String,
        description: String
    ) {
        // Update CraneDetails with provided image URL
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
        db.reference.child("Crane details").child(receivedValue)
            .setValue(craneDetails)
            .addOnSuccessListener {
                // Update the image URL in dataModel
                val dataModel = dataModel(model, imageUrl, description)
                db.reference.child("Model and Image").child(receivedValue)
                    .setValue(dataModel)
                    .addOnSuccessListener {
                        // Both CraneDetails and dataModel updated successfully
                        Toast.makeText(this, "Crane details updated", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.uploadImg.setImageURI(imageUri)  // Set the image in the ImageView

            // Upload the new image to Firebase storage and update the database
            imageUri?.let { uri ->
                val imageRef = storageRef.child("crane_images/$receivedValue.jpg")
                imageRef.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image upload successful
                        imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                            // Update the image URL in the database
                            val imageUrlString = imageUrl.toString()
                            db.reference.child("Crane details").child(receivedValue).child("image")
                                .setValue(imageUrlString)
                            db.reference.child("Model and Image").child(receivedValue).child("image")
                                .setValue(imageUrlString)

                                .addOnCompleteListener {
                                    // Delete the previous image from storage if it exists
                                    val previousImageRef = storageRef.child("crane_images/$receivedValue.jpg")
                                    previousImageRef.delete().addOnSuccessListener {
                                        // Previous image deleted successfully
                                        Toast.makeText(this, "Image replaced successfully", Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        // Failed to delete previous image
                                        Toast.makeText(this, "Failed to delete previous image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Image upload failed
                        Toast.makeText(
                            this,
                            "Image upload failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
