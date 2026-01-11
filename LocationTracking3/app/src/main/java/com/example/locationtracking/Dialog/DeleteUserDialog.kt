package com.example.locationtracking.Dialog

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.locationtracking.databinding.DeleteuserDialogBinding
import com.example.locationtracking.databinding.LogoutdialogBinding

class DeleteUserDialog constructor(activity: Activity, onClick: (String) -> Unit) {
    var dialog = Dialog(activity)
    var binding: DeleteuserDialogBinding =
        DeleteuserDialogBinding.inflate(LayoutInflater.from(activity))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        binding.apply {
            btncancel.setOnClickListener {
                dialog.setCancelable(false)
                dialog.dismiss()}
            btnlogout.setOnClickListener {
                onClick("Delete")
                dialog.setCancelable(true)
                dialog.dismiss()
            }
        }
        dialog.show()

    }

}