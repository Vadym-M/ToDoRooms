package com.vinade.todorooms.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vinade.todorooms.model.Card
import com.vinade.todorooms.fragment.CardFragment
import com.vinade.todorooms.database.DataBase
import com.vinade.todorooms.R

class CardAdapter(private val data: ArrayList<Card>, private val context: Context, private val roomID: String, private val fragment: CardFragment): RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    class ViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        val date: TextView
        val text: TextView
        val title: TextView
        val btnRemove: Button
        val container : LinearLayout
        val db: DataBase
        val cardItem: CardView

    init {
        date = view.findViewById(R.id.card_date)
        text = view.findViewById(R.id.card_text)
        title = view.findViewById(R.id.card_title)
        btnRemove = view.findViewById(R.id.card_remove_item)
        container = view.findViewById(R.id.card_container)
        cardItem = view.findViewById(R.id.card_item)
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
        holder.container.setOnClickListener {
            fragment.intentToCreateActivityFromRecycler(holder.cardItem, data.title, data.text, data.id)
        }

    }

    override fun getItemCount() = data.size
}