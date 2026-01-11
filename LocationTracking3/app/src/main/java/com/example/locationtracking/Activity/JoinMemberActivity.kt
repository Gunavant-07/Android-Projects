package com.example.locationtracking.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.ui.graphics.vector.Group
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.ModelData.CircleJoin
import com.example.locationtracking.ModelData.Circledata
import com.example.locationtracking.ModelData.Circles
import com.example.locationtracking.ModelData.CreateUser
import com.example.locationtracking.R
import com.example.locationtracking.Utils.tos
import com.example.locationtracking.databinding.ActivityJoinMemberBinding
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

class JoinMemberActivity : BaseAct<ActivityJoinMemberBinding>() {

    private var code = ""
    var reference: DatabaseReference? = null
    var currentReference: DatabaseReference? = null
    var user: FirebaseUser? = null
    var auth: FirebaseAuth? = null
    var current_user_id: String? = null
    var join_user_id: String? = null
    var circleReference: DatabaseReference? = null
    var circleReference1: DatabaseReference? = null
    override fun getActivityBinding(inflater: LayoutInflater) =
        ActivityJoinMemberBinding.inflate(layoutInflater)

    @SuppressLint("ResourceAsColor")
    override fun initUI() {
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser


        reference = FirebaseDatabase.getInstance().reference.child("Users")
        currentReference = FirebaseDatabase.getInstance().reference.child("Users").child(user!!.uid)
        current_user_id = user!!.uid

        binding.btnSubmit.setOnClickListener { submitButtonClick() }

        binding.pinView.setTextColor(R.color.black)
    }

    @SuppressLint("RestrictedApi")
    fun submitButtonClick() {

        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    "hello".log()
                    // Iterate over the children of "Circles"
                    for (circleSnapshot in dataSnapshot.children) {
                        "hii".log()
                        val userid = circleSnapshot.key

                        reference!!.child(userid!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        val circlesSnapshot = dataSnapshot.child("Circles")
                                        if (circlesSnapshot.exists()) {
                                            for (circleSnapshot in circlesSnapshot.children) {
                                                val circleName = circleSnapshot.key
                                                val circleCode = circleSnapshot.child("code")
                                                    .getValue(String::class.java)
                                                if (circleName != null && circleCode != null) {
                                                    "Circle name: $circleName, Code: $circleCode".log()
                                                    val query: Query =
                                                        reference!!.child(userid).child("Circles").child(circleName).orderByChild("code").equalTo(binding.pinView.value)
                                                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {

                                                        }

                                                        override fun onCancelled(error: DatabaseError) {}
                                                    })
                                                } else {
                                                    "Circle name or code not found for child: ${circleSnapshot.key}".log()
                                                }
                                            }
                                        } else {
                                            "No Circles found for the user".log()
                                        }
                                    } else {
                                        "User data not found".log()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle error
                                }
                            })
                    }
                } else {
                    "No groups found under 'Circles'".log()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })


      reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var createUser: CreateUser


                    for (childs in snapshot.children) {

                        createUser = childs.getValue(CreateUser::class.java)!!
                        join_user_id = createUser.userId


                        "userid - - ${childs.key}".log()
                        val circleName =childs.child("Circles").children


                        circleName!!.forEach {
                            val circlename =it.key!!
                            val circleCode = childs.child("Circles").child(circlename!!).child("code").getValue(String::class.java)
                            "name - - $circlename".log()
                            "coed-- $circleCode".log()
                            "semcode1 - - ${binding.pinView.value}".log()

                                    "$snapshot".log()
                                    "semcode1 - - ${binding.pinView.value}".log()
                                    if (binding.pinView.value==circleCode)
                                    {
                                        "hi".log()
                                        circleReference = FirebaseDatabase.getInstance().reference.child("Users").child(join_user_id!!).child("Circles").child(circlename)

                                        circleReference1 =FirebaseDatabase.getInstance().reference.child("Users").child(current_user_id!!).child("Circles").child(circlename)
                                        val circleJoin = CircleJoin(current_user_id)
                                        val circleJoin1 = CircleJoin(join_user_id)

                                        circleReference!!.child(current_user_id!!).setValue(circleJoin)
                                            .addOnCompleteListener { task: Task<Void?> ->
                                                if (task.isSuccessful) {
                                                    startActivity(Intent(this@JoinMemberActivity,MainActivity::class.java))
                                                    finish()
                                                }
                                            }
                                        circleReference1!!.child(join_user_id!!).setValue(circleJoin1)
                                            .addOnCompleteListener { task: Task<Void?> ->
                                                if (task.isSuccessful) { Toast.makeText(this@JoinMemberActivity, "User Joined Circle Successfully", Toast.LENGTH_SHORT).show()
//                                                    "user1 = = ${circleJoin1.circlememberid}".log()
//                                                    "user = = ${circleJoin.circlememberid}".log()
                                                }
                                            }


                                    }else{
                                        "no match".log()
                                    }


                        }


//                        if (circleName != null && circleCode != null) {
//                            "Circle name: $circleName, Code: $circleCode".log()
//                            val query: Query =
//                                reference!!.child(userid).child("Circles").child(circleName).orderByChild("code").equalTo(binding.pinView.value)
//                            query.addListenerForSingleValueEvent(object : ValueEventListener {
//                                override fun onDataChange(snapshot: DataSnapshot) {
//
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {}
//                            })
//                        } else {
//                            "Circle name or code not found for child: ${circleSnapshot.key}".log()
//                        }


                    }
                } else {
                    "Circle code is invalid".tos()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

    }
}