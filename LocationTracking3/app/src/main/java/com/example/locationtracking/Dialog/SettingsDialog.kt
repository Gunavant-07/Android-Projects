package com.example.locationtracking.Dialog

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams
import com.example.locationtracking.databinding.SettingPermissionBinding

class SettingsDialog constructor(activity: Activity, onClick: (String) -> Unit) {
    var dialog = Dialog(activity)
    var binding: SettingPermissionBinding =
        SettingPermissionBinding.inflate(LayoutInflater.from(activity))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window!!.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        binding.btnSetting.setOnClickListener {
            onClick("Settings")
        }

        dialog.show()

    }

    fun dialogDismiss(){
        dialog.dismiss()
    }
}