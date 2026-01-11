package com.example.locationtracking.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locationtracking.Activity.Application.Companion.log
import com.example.locationtracking.Activity.MainActivity
import com.example.locationtracking.ModelData.Circledata
import com.example.locationtracking.R
import com.example.locationtracking.databinding.CircleItemBinding

class SpinnerAdapter(val activity: MainActivity, val circlelist: ArrayList<Circledata>) : RecyclerView.Adapter<SpinnerAdapter.SpinnerData>(),
    android.widget.SpinnerAdapter {

    class SpinnerData(val binding: CircleItemBinding): RecyclerView.ViewHolder(binding.root)

    private val observers = mutableListOf<DataSetObserver>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= SpinnerData(
        CircleItemBinding.inflate(
            LayoutInflater.from(activity)
        )
    )

    override fun onBindViewHolder(holder: SpinnerData, position: Int) {
        holder.binding.apply {
            val name =circlelist[position].circlename
            txtcirclename.text=name
        }
    }

    override fun getItemCount() = circlelist.size
    override fun registerDataSetObserver(p0: DataSetObserver?) {
        p0?.let { observers.add(it) }
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
        p0?.let { observers.remove(it) }
    }

    override fun getCount()=circlelist.size

    override fun getItem(p0: Int): Any {
        return circlelist[p0]
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.circle_item, parent, false)
        // Bind data to the view
        val circleData = circlelist[position].circlename
        itemView.findViewById<TextView>(R.id.txtcirclename).text = circleData
        return itemView
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getDropDownView(position: Int, p1: View?, parent: ViewGroup?): View {
        val dropDownItemView = LayoutInflater.from(parent?.context).inflate(R.layout.spinnerdropdown_item, parent, false)
        // Bind data to the view
        val circleData = circlelist[position].circlename
        dropDownItemView.findViewById<TextView>(R.id.txtdropcirclename).text = circleData
        return dropDownItemView
    }


}