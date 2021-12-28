package com.vinade.todorooms

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.transition.Transition
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlin.concurrent.thread

class TaskAdapter(val task:Task, val context: Context?, val fragment: TaskFragment): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var roomID = ""
    var dataList = ArrayList<Task>()
    init {
        if (task.items.size == 0){
            val item = ItemTask("You don't have subtasks, click here to create new")
            item.id = "empty"
            task.items.add(item)
        }
    }

    companion object{
        const val HEADER = 0
        const val ITEM = 1
    }
    lateinit var headerTextView:TextView
    var isExpanded: Boolean = false
    var isSecondAnim: Boolean = false
    var isFirstClick: Boolean = false
    var isRotate: Boolean = false

    fun initRoomID(id:String){
        roomID = id
    }


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        class HeaderViewHolder(itemView: View): ViewHolder(itemView){
            private val headerTextView = itemView.findViewById<TextView>(R.id.title_task)
            private val btnAddItem = itemView.findViewById<Button>(R.id.add_item)
            private val btnOptoinItem = itemView.findViewById<TextView>(R.id.textViewOptions)
            private val rootLayoutOfTitle = itemView.findViewById<ConstraintLayout>(R.id.layout_of_title_task)
            private val itemsSize = itemView.findViewById<TextView>(R.id.items_size)
            private val arrow = itemView.findViewById<ImageView>(R.id.arrow_task)
            lateinit var context:Context
            lateinit var roomID:String
            val db = initDatabase()
            var secondAnim:Boolean = false

            fun initContext(context: Context){
                this.context = context
            }
            fun initRoomID(id : String){
                roomID = id
            }
            @SuppressLint("SetTextI18n")
            fun onBind(task: Task, onClickListener: View.OnClickListener, fragmentLayout: RelativeLayout, adapter: TaskAdapter, rootFragment: TaskFragment){
                adapter.initHeaderTextView(headerTextView)
                if(arrow.rotation == 180.0f){
                    arrow.rotation = 0.0f
                }
                if(adapter.isFirstClick) {
                    adapter.startAnim(arrow)
                }
                if(adapter.isRotate){
                    arrow.rotation = 180f
                    adapter.isRotate = false
                }


                headerTextView.text = task.title
                val size = task.items.size.toString()

                var sizeDone = 0
                for (item in task.items){
                    if(item.isDone){
                        sizeDone++
                    }
                }
                itemsSize.text = "$sizeDone/$size"

                itemView.setOnClickListener {
                    adapter.isFirstClick = true
                   onClickListener.onClick(it)
                }
                btnAddItem.setOnClickListener {
                    rootFragment.intentToItemActivity(headerTextView, task.id, roomID)
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
            val color = itemCheckBoxView.textColors

            fun onBind(item: ItemTask, roomID: String, task:Task, fragment: TaskFragment, adapter: TaskAdapter){
                itemCheckBoxView.text = item.text

                if(item.isDone){
                    itemCheckBoxView.isChecked = true
                    itemCheckBoxView.setTextColor(Color.GRAY)
                    itemCheckBoxView.paintFlags = itemCheckBoxView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    itemCheckBoxView.isChecked = false
                    itemCheckBoxView.setTextColor(color)
                    itemCheckBoxView.paintFlags = 0
                }

                itemCheckBoxView.setOnClickListener {
                    if(!item.id.equals("empty")) {
                        fragment.doneItem(item.id, task)
                    }else{
                        itemCheckBoxView.isChecked = false
                        fragment.intentToItemActivity(adapter.headerTextView, task.id, roomID)
                    }

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
            holder.onBind(task, onHeaderClicked(), fragment.getRootLayout(), this, fragment)

        }
        is ViewHolder.ItemViewHolder -> {
            holder.onBind(task.items[position-1], roomID, task, fragment, this)
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
            isRotate = true
            isSecondAnim = true
            notifyItemRangeInserted(1, task.items.size)
            notifyItemChanged(0)

        }else{
            notifyItemRangeRemoved(1, task.items.size)
            notifyItemChanged(0)

        }

    }

    fun startAnim(view: View){
        if(!isSecondAnim ){
            val rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_arrow)
            if(isRotate){
            }
            view.startAnimation(rotation)
            isSecondAnim = !isSecondAnim
        }else{
            val rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_arrow_down)
            view.startAnimation(rotation)
            isSecondAnim = !isSecondAnim
        }


    }
    fun initHeaderTextView(view: View){
        headerTextView = view as TextView
    }
}