package com.example.locationtracking.Activity

import android.content.Intent
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.ModelData.CreateUser
import com.example.locationtracking.databinding.ActivitySigninBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.mindrot.jbcrypt.BCrypt

class SigninActivity : BaseAct<ActivitySigninBinding>() {

    var email = ""
    var pass = ""
    var auth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var userId: String? = null
    var reference: DatabaseReference? = null
    var storageReference: StorageReference? = null

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivitySigninBinding.inflate(layoutInflater)

    override fun initUI() {
        binding.apply {
            auth = Firebase.auth
            reference = FirebaseDatabase.getInstance().reference.child("Users")
            storageReference = FirebaseStorage.getInstance().reference.child("User_Images")

            btnsignup.setOnClickListener {

                if (edtemail.text.toString() != "") {
                    if (isValidEmail(edtemail.text.toString())) {

                        if (edtpass.text.toString() != "") {
                            email = edtemail.text.toString()
                            pass = edtpass.text.toString()
                            val hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt())
                            createAccount(email, hashedPassword,it)
                        } else {
                            edtpass.error = "Please Enter Password"
                        }
                    } else {
                        edtemail.error = "Please Enter valid Email"
                    }
                } else {
                    edtemail.error = "Please Enter Email"
                }
            }

            btnlogin.setOnClickListener {
                startActivity(Intent(this@SigninActivity,LoginScreen::class.java))
                finish()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun createAccount(email: String, password: String, view: View) {
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user = auth!!.currentUser
                    assert(user != null)
                    val createUser = CreateUser(
                        "",
                        email,
                        password,
                        false,
                        0.0,
                        0.0,
                        "",
                        "",
                        "",
                        "",
                        "na",
                        user!!.uid
                    )
                    userId = user!!.uid
                    reference!!.child(userId!!).setValue(createUser)
                        .addOnCompleteListener { task1: Task<Void?> ->
                            if (task1.isSuccessful) {
                                "set success fully".log()
                            } else {
                                Toast.makeText(this, "can't Register.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    "createUserWithEmail:success".log()
                    startActivity(Intent(this@SigninActivity, NameActivity::class.java).putExtra("email",email).putExtra("password",password))
                    finish()
                } else {
//                    "The email address is already in use by another account.".tos()
                    "createUserWithEmail:failure = ${task.exception}".log()
                    Snackbar.make(view, "This Email is Already exist..", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

}