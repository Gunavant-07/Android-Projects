package com.example.locationtracking.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.locationtracking.R
import com.example.locationtracking.Utils.load
import com.example.locationtracking.databinding.ActivityMemberProfileBinding

class MemberProfileActivity : BaseAct<ActivityMemberProfileBinding>() {

    override fun getActivityBinding(inflater: LayoutInflater) =ActivityMemberProfileBinding.inflate(layoutInflater)

    override fun initUI() {
        binding.apply {

            back.setOnClickListener { finish() }
            var username =intent.getStringExtra("username")
            var city =intent.getStringExtra("city")
            var address =intent.getStringExtra("address")
            var network =intent.getStringExtra("network")
            var charge =intent.getStringExtra("charge")
            var latitude =intent.getDoubleExtra("lat",0.0)
            var longitude =intent.getDoubleExtra("lng",0.0)
            var email =intent.getStringExtra("email")
            var imageurl =intent.getStringExtra("imageurl")

            txtname.text = username
            txtemail.text =email
            txtlat.text = latitude.toString()
            txtlang.text = longitude.toString()
            txtaddress.text =address
            txtcity.text =city
            txtnetwork.text =network
            txtcharge.text =charge
            imguser.load(imageurl)
        }
    }
}