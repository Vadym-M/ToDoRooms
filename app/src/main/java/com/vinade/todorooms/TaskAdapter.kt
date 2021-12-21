package com.vinade.todorooms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

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
            lateinit var context:Context
            lateinit var roomID:String
            fun initContext(context: Context){
                this.context = context
            }
            fun initRoomID(id : String){
                roomID = id
            }

            fun onBind(header: Task, onClickListener: View.OnClickListener){
                headerTextView.text = header.title

                itemView.setOnClickListener {
                   onClickListener.onClick(it)
                }
                btnAddItem.setOnClickListener {
                    showBottomSheet(header)
                }
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
                    val db = DataBase()
                    task.addItem(item)
                    db.initDatabase()
                    db.writeNewTask(task,roomID)

                }

                dialog.setContentView(view)
                dialog.show()

            }
        }
        class ItemViewHolder(itemView: View): ViewHolder(itemView){
            private val itemTextView = itemView.findViewById<CheckBox>(R.id.item_of_task)

            fun onBind(item: ItemTask){
                itemTextView.text = item.text
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
            holder.onBind(task, onHeaderClicked())

        }
        is ViewHolder.ItemViewHolder -> {
            holder.onBind(task.items[position - 1])
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