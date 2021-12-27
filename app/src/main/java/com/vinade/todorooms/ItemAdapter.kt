package com.vinade.todorooms

import android.app.ActionBar
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import androidx.core.view.marginLeft
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.collection.LLRBNode

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
        //holder.item.rootView.setBackgroundColor(Color.WHITE)
        val params = ActionBar.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        //params.pad

        holder.item.rootView.setPadding(0,0,0,0)
        holder.item.text = data.text
        holder.item.setOnCheckedChangeListener { buttonView, isChecked ->
            itemActivity.removeItemTask(data)
        }
    }

    override fun getItemCount() = dataList.size
}