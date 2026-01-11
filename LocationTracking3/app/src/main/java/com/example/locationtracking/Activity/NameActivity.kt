package com.example.locationtracking.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.ModelData.CreateUser
import com.example.locationtracking.Utils.gon
import com.example.locationtracking.Utils.visible
import com.example.locationtracking.databinding.ActivityNameBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class NameActivity : BaseAct<ActivityNameBinding>() {

    var resultUri: Uri? = null

    var email: String? = null
    var password: String? = null
    var isSharing: String? = null
    var date: String? = null
    var code: String? = null
    var name: String? = null
    var imageUri: Uri? = null

    var auth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var userId: String? = null
    var reference: DatabaseReference? = null
    var storageReference: StorageReference? = null

    var dialog: ProgressDialog? = null

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityNameBinding.inflate(layoutInflater)

    override fun initUI() {

        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference.child("Users")
        storageReference = FirebaseStorage.getInstance().reference.child("User_Images")

        dialog = ProgressDialog(this)

        if (intent != null) {
            email = intent.getStringExtra("email")
            password = intent.getStringExtra("password")
        }

        binding.btnNext.setOnClickListener { registerUser() }
        binding.imageFilterView.setOnClickListener { selectImage() }
    }

//    fun generateCode() {
//
//        if (resultUri != null) {
//            startActivity(
//                Intent(this@NameActivity, InviteCodeActivity::class.java)
//                    .putExtra("name", binding.editTextTextPersonName.text.toString())
//                    .putExtra("email", email)
//                    .putExtra("date", date)
//                    .putExtra("isSharing", "false")
//                    .putExtra("code", code)
//                    .putExtra("password", password)
//                    .putExtra("imageUri", resultUri)
//            )
//            finish()
//        } else {
//            Toast.makeText(this, "choose image", Toast.LENGTH_SHORT).show()
//        }
//    }

    fun selectImage() {
        val i = Intent()
        i.action = Intent.ACTION_GET_CONTENT
        i.type = "image/*"
        startActivityForResult(i, 100)
    }

    fun registerUser() {

        if (resultUri != null) {

            if (binding.editTextTextPersonName.text.toString() != "") {
                dialog!!.setMessage("Please wait while we are creating an account for you.")
                dialog!!.show()
                dialog!!.setCancelable(false)
                user = auth!!.currentUser
                assert(user != null)
                val createUser =
                    CreateUser(
                        binding.editTextTextPersonName.text.toString(),
                        email,
                        password,
                        false,
                        0.0,
                        0.0,
                        "",
                        "",
                        "",
                        "",
                        imageUri.toString(),
                        user!!.uid
                    )
                userId = user!!.uid
                reference!!.child(userId!!).setValue(createUser)
                    .addOnCompleteListener { task1: Task<Void?> ->
                        if (task1.isSuccessful) {
                            // save the image to firebase
                            dialog!!.dismiss()
                            startActivity(Intent(this, PermissionActivity::class.java))
                            finish()
                        } else {
                            dialog!!.dismiss()
                            Toast.makeText(this, "can't Register.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                binding.editTextTextPersonName.error = "Please Enter Name"
            }


        } else {
            Toast.makeText(this, "choose image", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            binding.progress.visible()
            binding.materialTextView4.gon()
            binding.materialTextView3.gon()
            binding.btnNext.isClickable = false
            resultUri = data.data
            binding.imageFilterView.setImageURI(resultUri)
            val user = FirebaseAuth.getInstance().currentUser
            val storageRef =
                Firebase.storage.reference.child("images/${user?.uid}_profile.jpg")

            val uploadTask = storageRef.putFile(resultUri!!)

            uploadTask.addOnSuccessListener {
                // Image uploaded successfully
                Log.d("FATZ", "Image uploaded successfully")
                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        // Handle the download URL
                        "uri = $uri".log()
                        this.imageUri = uri
                        binding.progress.gon()
                        binding.btnNext.isClickable = true
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors
                        Log.e("FATZ", "Error getting download URL: $exception")
                    }


            }.addOnFailureListener {
                // Handle unsuccessful uploads
                Log.e("FATZ", "Failed to upload image")
            }
        }
    }
}