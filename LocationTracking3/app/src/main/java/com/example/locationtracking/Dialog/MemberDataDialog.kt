package com.example.locationtracking.Dialog

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import com.example.locationtracking.Utils.load
import com.example.locationtracking.databinding.MemberdatadialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MemberDataDialog(
    activity: Activity,
    username: String,
    userid: String,
    city: String,
    charge: String,
    network: String,
    image: String,
    onClick: (String) -> Unit,
    onDirection: (String) -> Unit,
    onInfo: (String) -> Unit,
) {
    var dialog = BottomSheetDialog(activity)
    var binding: MemberdatadialogBinding =
        MemberdatadialogBinding.inflate(LayoutInflater.from(activity))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        binding.apply {

            txtusername.text=username
            txtcurrentcity.text =city
            txtcharg.text =charge
            txtnetwork.text =network
            txtowner.text ="Member"
            imguser.load(image)
            imgdirection.setOnClickListener {
                onDirection("Direction")
                dialog.setCancelable(true)
                dialog.dismiss()
            }
            imgdelete.setOnClickListener {
                onClick("Settings")
                dialog.setCancelable(true)
                dialog.dismiss()
            }
            imgclose.setOnClickListener {
                dialog.dismiss()
            }
            imginfo.setOnClickListener {
                onInfo("Info")
                dialog.setCancelable(true)
            }
        }

        dialog.show()

    }

}