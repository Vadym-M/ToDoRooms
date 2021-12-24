package com.vinade.todorooms

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.vinade.todorooms.CardAdapter.*

class CardAdapter(private val data: ArrayList<Card>, private val context: Context, private val roomID: String): RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    class ViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        val date: TextView
        val text: TextView
        val title: TextView
        val btnRemove: Button
        val db: DataBase

    init {
        date = view.findViewById(R.id.card_date)
        text = view.findViewById(R.id.card_text)
        title = view.findViewById(R.id.card_title)
        btnRemove = view.findViewById(R.id.card_remove_item)
        db = DataBase()
        db.initDatabase()
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
            Log.d("tag", "BUTTON WORK")
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Remove this card?")
            //alertDialogBuilder.setPositiveButton("Remove", DialogInterface.OnClickListener(function = x))
            alertDialogBuilder.setPositiveButton("remove") { dialog, which ->
                holder.db.removeCard(roomID, data)
                Toast.makeText(context,
                    "Card removed!", Toast.LENGTH_SHORT).show()
            }
            alertDialogBuilder.setNegativeButton("cancel"){ dialog, which ->

            }
            alertDialogBuilder.show()

        }

    }

    override fun getItemCount() = data.size
}