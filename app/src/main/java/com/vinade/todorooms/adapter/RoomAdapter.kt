package com.vinade.todorooms.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vinade.todorooms.R
import com.vinade.todorooms.model.Room
import com.vinade.todorooms.activity.RoomActivity

class RoomAdapter (var context: Context) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    var dataList = emptyList<Room>()

    internal fun setRoomList(dataList: List<Room>) {
        this.dataList = dataList
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView
        var item: LinearLayout
        init {
            item = itemView.findViewById(R.id.itemLayout)
            title = itemView.findViewById(R.id.element_title)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        var view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_activity_main, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        var data = dataList[position]


        holder.item.setOnClickListener {
            showBottomSheet(data)
        }
        holder.title.text = data.name.toString()

    }
    fun showBottomSheet(room: Room){
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val dialog = BottomSheetDialog(context)
        val view = inflater.inflate(R.layout.bottom_sheet_room, null)

        val title = view.findViewById<TextView>(R.id.room_title)
        val desc = view.findViewById<TextView>(R.id.room_description)
        val btnJoin = view.findViewById<Button>(R.id.roomJoin)
        val btnCancel = view.findViewById<Button>(R.id.roomCancel)

        title.text = room.name
        desc.text = room.description

        btnJoin.setOnClickListener {
            val intent = Intent(context, RoomActivity::class.java)
            intent.putExtra("roomID", room.id)
            intent.putExtra("roomTitle", room.name)

            context.startActivity(intent)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()


    }

    override fun getItemCount() = dataList.size
}