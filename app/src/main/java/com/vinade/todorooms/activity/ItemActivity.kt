package com.vinade.todorooms.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vinade.todorooms.database.DataBase
import com.vinade.todorooms.R
import com.vinade.todorooms.adapter.ItemAdapter
import com.vinade.todorooms.model.ItemTask
import com.vinade.todorooms.model.Task

class ItemActivity : AppCompatActivity() {
    var dataList = arrayListOf<ItemTask>()
    var db = DataBase()
    lateinit var roomId: String
    lateinit var taskId: String
    lateinit var input:EditText
    lateinit var task: Task
    lateinit var reflatitude: DatabaseReference
    lateinit var listener: ValueEventListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        supportActionBar?.hide()
        val title = findViewById<TextView>(R.id.title_of_task)
        input = findViewById<EditText>(R.id.input_item)
        val btnAdd = findViewById<Button>(R.id.button_add_item)
        val btnDone = findViewById<Button>(R.id.items_done)
        val recycler = findViewById<RecyclerView>(R.id.recyclerOfItem)
        val lManager = LinearLayoutManager(this)
        lManager.reverseLayout = true
        lManager.stackFromEnd = true
        recycler.layoutManager = lManager
        roomId = intent.getStringExtra("idRoom").toString()
        taskId = intent.getStringExtra("idTask").toString()
        Handler(Looper.getMainLooper()).postDelayed({
            keyboardManager(true)
        }, 500)

        db.initDatabase()
        val database = Firebase.database
        reflatitude = database.getReference().child("Rooms").child(roomId!!).child("tasks").child(taskId!!)
        listener = (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("tag", "I AM IN DATACHANGE")
                task = snapshot.getValue(Task::class.java)!!

                val fList = mutableListOf<ItemTask>()
                val sList = mutableListOf<ItemTask>()
                for(item in task.items){
                    if(item.isDone){
                        fList.add(item)
                    }else{
                        sList.add(item)
                    }
                }
                val concat = fList + sList
                val adapter = ItemAdapter(concat as ArrayList<ItemTask>, this@ItemActivity)

                recycler.adapter = adapter
                title.text = task.title



                btnAdd.setOnClickListener {
                    val data = input.text.toString()
                    val item = ItemTask(data)
                    task.addItem(item)
                    db.writeNewTask(task,roomId)
                    input.text.clear()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        reflatitude.addValueEventListener(listener)
        btnDone.setOnClickListener {
            keyboardManager(false)
            finish()
        }

    }

    fun removeItemTask(item: ItemTask){
        val isDone = item.isDone
        for(it in task.items){
            if(it.id == item.id){
                it.isDone = !isDone
            }
        }
        db.updateItems(roomId,taskId, task.items)
    }
    fun keyboardManager(show:Boolean) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(show){
            input.requestFocus()
            manager.showSoftInput(input, InputMethodManager.SHOW_FORCED)
        }else{
            manager.hideSoftInputFromWindow(input.applicationWindowToken, 0)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        keyboardManager(false)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        reflatitude.removeEventListener(listener)
    }
}