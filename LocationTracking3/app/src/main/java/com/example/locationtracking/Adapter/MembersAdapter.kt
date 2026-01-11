package com.example.locationtracking.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.Activity.MainActivity
import com.example.locationtracking.ModelData.MemberData
import com.example.locationtracking.R
import com.example.locationtracking.Utils.load
import com.example.locationtracking.Utils.loadImg
import com.example.locationtracking.databinding.MemberItemBinding
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class MembersAdapter(
    val activity: MainActivity,
    val list: MutableList<MemberData>,
    val urlcode:Int,
    var onGet: (Int) -> Unit,
    var onDatachange: (Int) -> Unit,
    var onClick: (Int) -> Unit
) : RecyclerView.Adapter<MembersAdapter.MemberView>() {
    class MemberView(val binding: MemberItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MemberView(MemberItemBinding.inflate(LayoutInflater.from(activity)))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: MemberView, position: Int) {
        holder.binding.apply {

            if (urlcode==100)
            {
                val storageRef = Firebase.storage.reference.child("images/${list[position].userid}_profile.jpg")
                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        // Handle the download URL
                        val imageUrl = uri.toString()
                        imguser.loadImg(imageUrl)
                        onDatachange(0)
                        "ImageUrl = $imageUrl".log()
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors
                        Log.e("FATZ", "Error getting download URL: $exception")
                    }
            }


            var name = list[position].username

            val firstName = name
            if (firstName.length > 8) {
                // Display only the first 8 letters followed by "..."
                val truncatedText: String =
                    firstName.substring(0, 8) + "..."
                txtname.setText(truncatedText)
            } else {
                txtname.setText(firstName)
            }
            root.setOnClickListener {
                onGet(position)
                onClick(position)
            }
        }
    }

    fun setData(newData: List<MemberData>) {
        val distinctData = newData.distinct()
        list.clear()
        list.addAll(distinctData)
        notifyDataSetChanged()
    }
}