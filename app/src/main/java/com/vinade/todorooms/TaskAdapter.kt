package com.vinade.todorooms

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class TaskAdapter(val task:Task, val context: Context?, val fragment: TaskFragment): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var roomID = ""
    var dataList = ArrayList<Task>()


    companion object{
        const val HEADER = 0
        const val ITEM = 1
    }

    var isExpanded: Boolean = false

    fun initRoomID(id:String){
        roomID = id
    }


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        class HeaderViewHolder(itemView: View): ViewHolder(itemView){
            private val headerTextView = itemView.findViewById<TextView>(R.id.title_task)
            private val btnAddItem = itemView.findViewById<Button>(R.id.add_item)
            private val btnOptoinItem = itemView.findViewById<TextView>(R.id.textViewOptions)
            lateinit var context:Context
            lateinit var roomID:String
            val db = initDatabase()
            fun initContext(context: Context){
                this.context = context
            }
            fun initRoomID(id : String){
                roomID = id
            }

            fun onBind(task: Task, onClickListener: View.OnClickListener, fragmentLayout: RelativeLayout){
                headerTextView.text = task.title
                itemView.setOnClickListener {
                   onClickListener.onClick(it)
                }
                btnAddItem.setOnClickListener {
                    showBottomSheet(task)
                }
                btnOptoinItem.setOnClickListener {
                    val popup = PopupMenu(context, btnOptoinItem)
                    popup.inflate(R.menu.options_menu)
                    popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                        when (item!!.itemId) {
                            R.id.edit_item -> {
                                showBottomSheetEditTitle(task, fragmentLayout)

                            }
                            R.id.remove_item -> {
                                removeTask(task, fragmentLayout)
                            }
                            R.id.done_item -> {
                                removeAllItems(task, fragmentLayout)
                            }
                        }

                        true
                    })

                    popup.show()
                }

            }
            fun removeTask(task: Task, layout: RelativeLayout){
                db.removeTask(roomID, task)
                Snackbar.make(layout, "${task.title} removed!", Snackbar.LENGTH_SHORT).show()
            }
            fun removeAllItems(task: Task, layout: RelativeLayout){
                db.removeAllItems(roomID, task)
                Snackbar.make(layout, "All done!", Snackbar.LENGTH_SHORT).show()
            }
            fun initDatabase(): DataBase{
                val db = DataBase()
                db.initDatabase()
                return db
            }

            fun showBottomSheet(task:Task){
                val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                val dialog = BottomSheetDialog(context)
                val view = inflater.inflate(R.layout.bottom_sheet_create_item, null)

                val title = view.findViewById<TextView>(R.id.title_of_task)
                val input = view.findViewById<TextView>(R.id.input_item)
                val btnAdd = view.findViewById<Button>(R.id.button_add_item)


                title.text = task.title


                btnAdd.setOnClickListener {
                    val data = input.text.toString()
                    val item = ItemTask(data)
                    task.addItem(item)
                    db.writeNewTask(task,roomID)

                }

                dialog.setContentView(view)
                dialog.show()

            }
            fun showBottomSheetEditTitle(task: Task, layout: RelativeLayout){
                val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                val dialog = BottomSheetDialog(context)
                val view = inflater.inflate(R.layout.bottom_sheet_edit_task_title, null)
                val editText = view.findViewById<EditText>(R.id.editText_edit_title)
                val btn = view.findViewById<Button>(R.id.btn_edit_task_title)
                editText.setText(task.title)
                editText.requestFocus()
                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                dialog.setContentView(view)
                dialog.show()
                btn.setOnClickListener {
                    val db = DataBase()
                    db.initDatabase()
                    db.updateTitleTask(roomID, task, editText.text.toString())
                    dialog.dismiss()
                    Snackbar.make(layout, "Title changed!", Snackbar.LENGTH_SHORT).show()
                }


        }

        }

        class ItemViewHolder(itemView: View): ViewHolder(itemView){
            private val itemCheckBoxView = itemView.findViewById<CheckBox>(R.id.item_of_task)

            fun onBind(item: ItemTask, roomID: String, task:Task, fragment: TaskFragment){
                itemCheckBoxView.text = item.text

                itemCheckBoxView.setOnCheckedChangeListener { buttonView, isChecked ->
                   fragment.removeItem(item.id, task)

                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> {
                return ViewHolder.HeaderViewHolder(
                    layoutInflater.inflate(R.layout.row_layout_task_fragment, parent, false)
                )
            }
            else -> {
                return ViewHolder.ItemViewHolder(
                    layoutInflater.inflate(R.layout.item_of_task, parent, false)
                )
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    when(holder){
        is ViewHolder.HeaderViewHolder -> {
            context?.let { holder.initContext(it) }
            holder.initRoomID(roomID)
            holder.onBind(task, onHeaderClicked(), fragment.getRootLayout())

        }
        is ViewHolder.ItemViewHolder -> {
            holder.onBind(task.items[position-1], roomID, task, fragment)
        }
    }
    }


    override fun getItemViewType(position: Int): Int {
     return if(position == 0){
         HEADER
     }else{
         ITEM
     }
    }

    override fun getItemCount(): Int{
        return if(isExpanded){
            dataList.size+1
        }else{
            1
        }
    }

    private fun onHeaderClicked() = object : View.OnClickListener{
        override fun onClick(view: View?) {
            changeExpand()
        }
    }
    fun changeExpand(){
        isExpanded = !isExpanded

        if(isExpanded){
            notifyItemRangeInserted(1, task.items.size)
            notifyItemChanged(0)
            fragment.addExpand(task.id)
        }else{
            notifyItemRangeRemoved(1, task.items.size)
            notifyItemChanged(0)
            fragment.removeExpand(task.id)
        }
    }
    fun changeExpandFromLoop(){
        isExpanded = !isExpanded

        if(isExpanded){
            notifyItemRangeInserted(1, task.items.size)
            notifyItemChanged(0)

        }else{
            notifyItemRangeRemoved(1, task.items.size)
            notifyItemChanged(0)

        }

    }
}