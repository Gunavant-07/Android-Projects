package com.example.locationtracking.Activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.databinding.ActivityLoginscreenBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginScreen : BaseAct<ActivityLoginscreenBinding>() {

    var loginemail = ""
    var loginpass = ""
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityLoginscreenBinding.inflate(layoutInflater)

    override fun initUI() {
        binding.apply {
            auth = Firebase.auth

            btnlogin.setOnClickListener {
                if (edtemail.text.toString() != "") {
                    if (edtpass.text.toString() != "") {
                        loginemail = edtemail.text.toString()
                        loginpass = edtpass.text.toString()
                        signIn(loginemail, loginpass, it)
                    } else {
                        edtpass.error = "Please Enter Password"
                    }
                } else {
                    edtemail.error = "Please Enter Email"
                }
            }
            btnsignup.setOnClickListener {
                startActivity(Intent(this@LoginScreen, SigninActivity::class.java))
                finish()
            }
        }
    }

    private fun signIn(email: String, password: String, view: View) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    "signInWithEmail:success".log()
                    startActivity(
                        Intent(this@LoginScreen, PermissionActivity::class.java).putExtra(
                            "email",
                            email
                        ).putExtra("password", password)
                    )
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    "signInWithEmail:failure ${task.exception}".log()
                    "Authentication failed.".log()
                    // enter here snack bar
                    Snackbar.make(view, "Invalid Id or Pass..", Snackbar.LENGTH_SHORT).show()
                }
            }
        // [END sign_in_with_email]
    }
}