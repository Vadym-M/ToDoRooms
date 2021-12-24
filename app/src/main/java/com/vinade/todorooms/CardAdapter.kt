package com.vinade.todorooms

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vinade.todorooms.CardAdapter.*

class CardAdapter(private val data: ArrayList<Card>, private val context: Context): RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    class ViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        val date: TextView
        val text: TextView
        val title: TextView
        val btnRemove: Button

    init {
        date = view.findViewById(R.id.card_date)
        text = view.findViewById(R.id.card_text)
        title = view.findViewById(R.id.card_title)
        btnRemove = view.findViewById(R.id.card_remove_item)
    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = data[position]
        holder.date.setText(data.dateTime)
        holder.text.setText(data.text)
        holder.title.setText(data.title)
        holder.btnRemove.setOnClickListener {
            
        }

    }

    override fun getItemCount() = data.size
}