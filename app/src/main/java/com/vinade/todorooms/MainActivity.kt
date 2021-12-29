package com.vinade.todorooms

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var  roomAdapter: RoomAdapter
    private lateinit var db : DataBase

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_room_btn) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_room_btn) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    private var clicked = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        db = DataBase()
        db.initDatabase()

        title = "Rooms"

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(applicationContext,2)
        roomAdapter = RoomAdapter(this)
        db.readAllRooms(roomAdapter, recyclerView)
        Log.d("tag", "HERE: " + db.list)







        floatingBtn.setOnClickListener {
            onAddButtonClicked()
        }
        floatingBtnKey.setOnClickListener {

        }
        floatingBtnAddNew.setOnClickListener {

        }

    }

    private fun onAddButtonClicked() {
        setVisibility()
        setAnimation()
        clicked = !clicked

    }

    private fun setAnimation() {
     if(!clicked){
         floatingBtnAddNew.visibility = View.VISIBLE
         floatingBtnKey.visibility = View.VISIBLE
     }else{
         floatingBtnAddNew.visibility = View.GONE
         floatingBtnKey.visibility = View.GONE
     }
    }

    private fun setVisibility() {
      if(!clicked){
          floatingBtn.startAnimation(rotateOpen)
          floatingBtnKey.startAnimation(fromBottom)
          floatingBtnAddNew.startAnimation(fromBottom)
      }else{
          floatingBtn.startAnimation(rotateClose)
          floatingBtnKey.startAnimation(toBottom)
          floatingBtnAddNew.startAnimation(toBottom)
      }
    }


    fun showBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_create, null)

        val name = view.findViewById<EditText>(R.id.input_name)
        val description = view.findViewById<EditText>(R.id.input_descrip)
        val password = view.findViewById<EditText>(R.id.input_password)
        val btnCreate = view.findViewById<Button>(R.id.btnCreate)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        btnCreate.setOnClickListener {
            val name = name.text.toString()
            val description = description.text.toString()
            var password = password.text.toString()
            if (password == null){
                password = "null"
            }
            val card = mutableListOf<Card>()
            val testTask = Task("TEST")
            testTask.initId()
            val task: MutableList<Task> = ArrayList()
            task.add(testTask)
            val room = Room(name,description, password, card, task)
            val roomID = room.initId()
            db.writeNewRoom(room)
            val intent = Intent(this, RoomActivity::class.java)
            intent.putExtra("roomID",roomID)
            intent.putExtra("roomTitle",room.name)
            startActivity(intent)

        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val search = menu?.findItem(R.id.appSearchBar)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search"

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("",false)
                searchView.onActionViewCollapsed()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //adapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}