package com.example.locationtracking.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.Adapter.MembersAdapter
import com.example.locationtracking.ModelData.CircleJoin
import com.example.locationtracking.ModelData.CreateUser
import com.example.locationtracking.databinding.ActivityCreateMemberBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Random

class CreateMemberActivity : BaseAct<ActivityCreateMemberBinding>() {

    var reference: DatabaseReference? = null
    var currentReference: DatabaseReference? = null
    var user: FirebaseUser? = null
    var auth: FirebaseAuth? = null
    var current_user_id: String? = null
    var join_user_id: String? = null
    var circleReference: DatabaseReference? = null
    var circleReference1: DatabaseReference? = null
    private var code = ""

    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityCreateMemberBinding.inflate(layoutInflater)

    override fun initUI() {
        binding.apply {
            auth = FirebaseAuth.getInstance()
            user = auth!!.currentUser

            reference = FirebaseDatabase.getInstance().reference.child("Users")
            currentReference =
                FirebaseDatabase.getInstance().reference.child("Users").child(user!!.uid)
                    .child("Circles")
            current_user_id = user!!.uid

            btncreate.setOnClickListener {
                val random = Random()
                val n = 100000 + random.nextInt(900000)
                code = n.toString()

                if (edtcirclename.text.toString() != "") {
                    val circlename = edtcirclename.text.toString()
                    currentReference!!.child(circlename).child("code").setValue(code)
                    finish()
                } else {
                    edtcirclename.error = "Enter Circle Name"
                }
            }

        }
    }

}