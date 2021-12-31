package com.vinade.todorooms.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

class CardAdapter(private val data: ArrayList<Card>, private val context: Context, private val roomID: String, private val fragment: CardFragment): RecyclerView.Adapter<CardAdapter.ViewHolder>(), Filterable {
    var dataFilterList = ArrayList<Card>()
    val db =DataBase()
    init {
        dataFilterList = data
        db.initDatabase()
    }
    class ViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        val date: TextView
        val text: TextView
        val title: TextView
        val container : LinearLayout
        val db: DataBase
        val cardItem: CardView

    init {

        date = view.findViewById(R.id.card_date)
        text = view.findViewById(R.id.card_text)
        title = view.findViewById(R.id.card_title)
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = dataFilterList[position]
        val t = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.parse(data.dateTime, DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy", Locale.ENGLISH))
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        if(data.title.isEmpty()){
            holder.title.visibility = View.GONE
        }else{
            holder.title.setText(data.title)
        }
        val f: NumberFormat = DecimalFormat("00")
        holder.date.text = t.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
        //holder.date.text = "${f.format(t.hour)}:${f.format(t.minute) } ${t.dayOfWeek}"
        holder.text.setText(data.text)



        holder.container.setOnClickListener {
            fragment.intentToCreateActivityFromRecycler(holder.cardItem, data.title, data.text, data.id)
        }

    }
    fun deleteItem(position: Int){
        db.removeCard(roomID, dataFilterList[position])
        Toast.makeText(context,
            "Card removed!", Toast.LENGTH_SHORT).show()
    }

    fun getContext(): Context{
        return context
    }

    override fun getItemCount() = dataFilterList.size



    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    dataFilterList = data
                } else {
                    val resultList = ArrayList<Card>()
                    for (row in data) {
                        if (row.text.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT)) || row.title.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT)) ) {
                            resultList.add(row)
                        }
                    }
                    dataFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = dataFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                dataFilterList = results?.values as ArrayList<Card>
                notifyDataSetChanged()
            }

        }
    }
}