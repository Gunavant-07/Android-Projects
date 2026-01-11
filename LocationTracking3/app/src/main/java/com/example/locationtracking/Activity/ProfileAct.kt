package com.example.locationtracking.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.Adapter.GroupAdapter
import com.example.locationtracking.ModelData.GroupsName
import com.example.locationtracking.R
import com.example.locationtracking.Utils.gon
import com.example.locationtracking.Utils.loadImg
import com.example.locationtracking.Utils.visible
import com.example.locationtracking.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileAct : BaseAct<ActivityProfileBinding>() {

    var groupNameList = ArrayList<GroupsName>()
    var profileName: String = ""
    var imgUrl: String = ""
    var resultUri: Uri? = null
    var adapter: GroupAdapter? = null
    var dialog: ProgressDialog? = null
    val database = Firebase.database
    private var alluserid: Any? = null
    var firebaseAuth = com.google.firebase.ktx.Firebase.auth
    var user = firebaseAuth.currentUser
    val myRef = database.getReference("Users")

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityProfileBinding.inflate(layoutInflater)

    override fun initUI() {
        initClick()
        getData()
        changeValue()
    }

    private fun changeValue() {
        groupNameList.clear()
        binding.apply {
            txtName.isEnabled = false
            imgEdit.setOnClickListener {
                groupNameList.clear()
                txtName.isEnabled = true
                imgsave.visible()
                imgEdit.gon()
            }
            imgsave.setOnClickListener {
                groupNameList.clear()
                txtName.isEnabled = false
                imgsave.gon()
                imgEdit.visible()
                var name = txtName.text.toString()
                "$name".log()

                myRef.child(user?.uid!!)!!.child("name").setValue(name)
//                "name - - $icname".log()
                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (childrensnapshots in dataSnapshot.children) {
                                childrensnapshots.children.forEach {
                                    if (it.key == "userId") {
                                        alluserid = it.value
                                    }
                                }

                                if (user?.uid == alluserid) {
                                    childrensnapshots.child("Circles").children.forEach {

                                        if (it.exists()) {
                                            if (it.value.toString().contains("code")) {
                                                val code =
                                                    it.child("code").getValue(String::class.java)
                                                groupNameList.add(GroupsName(it.key!!, code!!))
                                                it.key.log()
                                                it.child("code").getValue(String::class.java).log()
                                            } else {
                                                "in code not available".log()
                                            }
                                        } else {
                                            "is not exist".log()
                                        }
                                    }
                                }
                            }
                            "List size: ${groupNameList.size}".log()
                            adapter = GroupAdapter(this@ProfileAct, groupNameList)
                            rvGrp.adapter = adapter

                        } else {
                            "is not exist".log()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle cancelled event
                        "on cancel".log()
                    }
                })
            }
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (childrensnapshots in dataSnapshot.children) {
                            childrensnapshots.children.forEach {
                                if (it.key == "userId") {
                                    alluserid = it.value
                                }
                            }

                            if (user?.uid == alluserid) {
                                childrensnapshots.child("Circles").children.forEach {

                                    if (it.exists()) {
                                        if (it.value.toString().contains("code")) {
                                            val code = it.child("code").getValue(String::class.java)
                                            groupNameList.add(GroupsName(it.key!!, code!!))
                                            it.key.log()
                                            it.child("code").getValue(String::class.java).log()
                                        } else {
                                            "in code not available".log()
                                        }
                                    } else {
                                        "is not exist".log()
                                    }
                                }
                            }
                        }
                        "List size: ${groupNameList.size}".log()
                        adapter = GroupAdapter(this@ProfileAct, groupNameList)
                        rvGrp.adapter = adapter

                    } else {
                        "is not exist".log()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle cancelled event
                    "on cancel".log()
                }
            })

        }
    }

    private fun getData() {
        binding.apply {
            profileName = intent.getStringExtra("name").toString()
            imgUrl = intent.getStringExtra("url").toString()

            imgPhoto.loadImg(imgUrl)
            txtName.setText(profileName)
        }
    }

    private fun initClick() {
        binding.apply {

            chooseImage.setOnClickListener {
                selectImage()
            }
            imgPhoto.setOnClickListener {
                selectImage()
            }

            imgBack.setOnClickListener { finish() }

        }
    }

    fun selectImage() {
        val i = Intent()
        i.action = Intent.ACTION_GET_CONTENT
        i.type = "image/*"
        startActivityForResult(i, 100)
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Function to compress a Bitmap
    private fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    // Function to save a Bitmap to a file
    private fun saveBitmapToFile(bitmap: Bitmap, filename: String) {
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Function to convert a Bitmap to a Base64 encoded string
    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
//            binding.progress.visible()
            dialog = ProgressDialog(this)
            dialog!!.setMessage("Please Wait")
            dialog!!.setCancelable(false)
            dialog!!.show()

            resultUri = data.data
            binding.imgPhoto.setImageURI(resultUri)


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
                        myRef.child(user?.uid!!).child("imageUrl").setValue(uri.toString())
                            .addOnCompleteListener {
                                "ïs complete".log()
                            }.addOnFailureListener {
                                "is fail".log()
                        }
                        dialog!!.dismiss()
//                        binding.progress.gon()
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