package com.vinade.todorooms

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ItemActivity : AppCompatActivity() {
    var dataList = arrayListOf<ItemTask>()
    var db = DataBase()
    lateinit var roomId: String
    lateinit var taskId: String
    lateinit var input:EditText
    lateinit var task: Task
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
        val ref = db.getReference()
        ref.child("Rooms").child(roomId!!).child("tasks").child(taskId!!).addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                    TODO("not implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    task = snapshot.getValue(Task::class.java)!!

                    val adapter = ItemAdapter(task.items, this@ItemActivity)

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
            }
        )
        btnDone.setOnClickListener {
            keyboardManager(false)
            finish()
        }

    }

    fun removeItemTask(item:ItemTask){
        task.items.remove(item)
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

}