package com.example.locationtracking.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.ModelData.CreateUser
import com.example.locationtracking.databinding.ActivityInviteCodeBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class InviteCodeActivity : BaseAct<ActivityInviteCodeBinding>() {

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
        ActivityInviteCodeBinding.inflate(layoutInflater)

    override fun initUI() {
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference.child("Users")
        storageReference = FirebaseStorage.getInstance().reference.child("User_Images")

        dialog = ProgressDialog(this)
        val intent = intent
        if (intent != null) {
            name = intent.getStringExtra("name")
            email = intent.getStringExtra("email")
            password = intent.getStringExtra("password")
            isSharing = intent.getStringExtra("isSharing")
            date = intent.getStringExtra("date")
            code = intent.getStringExtra("code")
        }

        binding.materialTextView.text = code

        val user = FirebaseAuth.getInstance().currentUser
        val storageRef = Firebase.storage.reference.child("images/${user?.uid}_profile.jpg")
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Handle the download URL
                "uri = $uri".log()
                this.imageUri = uri
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("FATZ", "Error getting download URL: $exception")
            }

        binding.button4.setOnClickListener { registerUser() }
    }

    fun registerUser() {
        dialog!!.setMessage("Please wait while we are creating an account for you.")
        dialog!!.show()
        dialog!!.setCancelable(false)

        user = auth!!.currentUser
        assert(user != null)
        val createUser =
            CreateUser(
                name,
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

    }

}