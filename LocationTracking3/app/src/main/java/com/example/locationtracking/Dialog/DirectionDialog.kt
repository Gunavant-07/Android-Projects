package com.example.locationtracking.Dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.locationtracking.databinding.MapdirectionDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class DirectionDialog(
    activity: Activity,
    onClick: (String) -> Unit
) {
    var dialog = BottomSheetDialog(activity)
    var binding: MapdirectionDialogBinding =
        MapdirectionDialogBinding.inflate(LayoutInflater.from(activity))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        binding.apply {

            btndirection.setOnClickListener {
                onClick("Direction")
            }

        }
        dialog.show()

    }

}