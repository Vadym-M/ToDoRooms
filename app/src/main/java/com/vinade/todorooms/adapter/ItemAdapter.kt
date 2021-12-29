package com.vinade.todorooms.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.vinade.todorooms.activity.ItemActivity
import com.vinade.todorooms.model.ItemTask
import com.vinade.todorooms.R

class ItemAdapter(private val dataList: ArrayList<ItemTask>, private val itemActivity: ItemActivity): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val item:CheckBox
        init {
            item = view.findViewById(R.id.item_of_task)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_of_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        if(data.isDone){
            holder.item.isChecked = true
            holder.item.paintFlags = holder.item.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        holder.item.rootView.setPadding(0,0,0,0)
        holder.item.text = data.text
        holder.item.setOnCheckedChangeListener { buttonView, isChecked ->
            itemActivity.removeItemTask(data)
        }
    }

    override fun getItemCount() = dataList.size
}