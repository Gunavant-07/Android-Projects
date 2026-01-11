package com.example.locationtracking.Adapter

import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.locationtracking.Activity.MainActivity
import com.example.locationtracking.databinding.CircleItemBinding

class CircleAdapter(val activity: MainActivity) :RecyclerView.Adapter<CircleAdapter.CircleData>(){

    class CircleData(val binding: CircleItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= CircleData(CircleItemBinding.inflate(LayoutInflater.from(activity)))


    override fun getItemCount() = 0

    override fun onBindViewHolder(holder: CircleAdapter.CircleData, position: Int) {
        holder.binding.apply {

        }
    }

}