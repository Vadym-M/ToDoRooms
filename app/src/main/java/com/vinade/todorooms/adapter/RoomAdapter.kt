package com.vinade.todorooms.adapter

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vinade.todorooms.R
import com.vinade.todorooms.RoomEditor
import com.vinade.todorooms.RoomElements
import com.vinade.todorooms.activity.RoomActivity
import com.vinade.todorooms.database.DataBase
import com.vinade.todorooms.model.Room
import com.vinade.todorooms.model.Task

class RoomAdapter (var context: Context) : RecyclerView.Adapter<RoomAdapter.ViewHolder>(), RoomElements, RoomEditor {

    var dataList = mutableListOf<Room>()
    var countDataTasks = mutableListOf<Int>()
    var countDataCards = mutableListOf<Int>()
    var isEdit = false
    var db = DataBase()
    lateinit var dialog:BottomSheetDialog


    lateinit var title_field : EditText
    lateinit var title_field_btn : ImageView
    lateinit var title_field_ok : TextView

    lateinit var description_field : EditText
    lateinit var description_field_btn : ImageView
    lateinit var description_field_ok : TextView

    lateinit var tasks_size: TextView
    lateinit var cards_size: TextView



    internal fun setRoomList( list:MutableList<Room>) {
        this.dataList = list
    }
    internal fun setCountTasks( list:MutableList<Int>) {
        this.countDataTasks = list
    }
    internal fun setCountCards( list:MutableList<Int>) {
        this.countDataCards = list
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

        var room = dataList[position]
        var countCards = countDataCards[position]
        var countTasks = countDataTasks[position]



        holder.item.setOnClickListener {
            showBottomSheet(room, countTasks, countCards)
        }
        holder.title.text = room.name.toString()

    }
    fun showBottomSheet(room: Room, tasks_count:Int, cards_count: Int){
        db.initDatabase()
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.bottom_sheet_room, null)
        dialog = BottomSheetDialog(context)

        title_field = view.findViewById<EditText>(R.id.room_title)
        title_field_btn = view.findViewById(R.id.btn_edit_room_title)
        title_field_ok = view.findViewById(R.id.ok_edit_room_title)

        description_field = view.findViewById(R.id.room_description)
        description_field_btn = view.findViewById(R.id.btn_edit_room_description)
        description_field_ok = view.findViewById(R.id.ok_edit_room_description)

        tasks_size = view.findViewById(R.id.tasks_size)
        cards_size = view.findViewById(R.id.cards_size)

        val id_room = view.findViewById<TextView>(R.id.id_room)
        val btn_copy = view.findViewById<ImageView>(R.id.btn_copy_id)
        val btn_share = view.findViewById<ImageView>(R.id.btn_share_id)
        btn_share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "This is identifier room: \n" + room.id )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
        btn_copy.setOnClickListener {
            copyTextToClipboard(room.id)
        }
        id_room.setText(room.id)

        tasks_size.setText(tasks_count.toString())
        cards_size.setText(cards_count.toString())
        dialog.setOnDismissListener {
            isEdit = false
        }
        title_field_btn.setOnClickListener {
            editTitle(room)
        }
        description_field_btn.setOnClickListener {
            editDescription(room)
        }


        val btnJoin = view.findViewById<Button>(R.id.roomJoin)
        val btnCancel = view.findViewById<Button>(R.id.roomCancel)

        title_field.setText(room.name)
        description_field.setText(room.description)



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
    fun keyboardManager(input: EditText ,show:Boolean) {

        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(show){
            manager.showSoftInput(input, InputMethodManager.SHOW_FORCED)
        }else{
            manager.hideSoftInputFromWindow(input.applicationWindowToken, 0)
        }

    }

    override fun editDescription(room: Room) {
        if (!isEdit){
            isEdit = !isEdit
            description_field.isEnabled = true
            description_field_btn.visibility = View.GONE
            description_field_ok.visibility = View.VISIBLE
            dialog.setCancelable(false)
            description_field_ok.setOnClickListener {
                db.updateRoomDescription(room, description_field.text.toString())
                keyboardManager(description_field, false)
                description_field_btn.visibility = View.VISIBLE
                description_field_ok.visibility = View.GONE
                description_field.isEnabled = false
                dialog.setCancelable(true)
                isEdit = !isEdit
            }
            description_field.requestFocus()
            description_field.setSelection(description_field.length())
            keyboardManager(description_field, true)

        }

    }

    override fun editTitle(room: Room) {
        if (!isEdit){
            isEdit = !isEdit
            title_field.isEnabled = true
            title_field_btn.visibility = View.GONE
            title_field_ok.visibility = View.VISIBLE
            dialog.setCancelable(false)
            title_field_ok.setOnClickListener {
                db.updateRoomTitle(room, title_field.text.toString())
                keyboardManager(title_field, false)
                title_field_btn.visibility = View.VISIBLE
                title_field_ok.visibility = View.GONE
                title_field.isEnabled = false
                dialog.setCancelable(true)
                isEdit = !isEdit
            }
            title_field.requestFocus()
            title_field.setSelection(title_field.length())
            keyboardManager(title_field, true)

        }
    }

    override fun showCountTasks() {

    }

    override fun showCountCards() {
    }

    private fun copyTextToClipboard(textToCopy: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

}