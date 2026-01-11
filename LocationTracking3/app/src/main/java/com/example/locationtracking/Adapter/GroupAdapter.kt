package com.example.locationtracking.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.ModelData.GroupsName
import com.example.locationtracking.databinding.ListItemGrpBinding

class GroupAdapter(val activity: Activity, private val groupNameList: ArrayList<GroupsName>) :
    RecyclerView.Adapter<GroupAdapter.GroupData>() {

    class GroupData(val binding: ListItemGrpBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GroupData(
        ListItemGrpBinding.inflate(
            LayoutInflater.from(parent.context)
        )
    )

    override fun onBindViewHolder(holder: GroupData, position: Int) {
        holder.binding.apply {
            val item = groupNameList[position]
            txtName.text = item.name
            txtCode.text = item.code
        }
    }

    override fun getItemCount() = groupNameList.size
}