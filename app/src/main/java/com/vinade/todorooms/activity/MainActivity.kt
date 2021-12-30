package com.vinade.todorooms.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout.make
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vinade.todorooms.database.DataBase
import com.vinade.todorooms.R
import com.vinade.todorooms.adapter.RoomAdapter
import com.vinade.todorooms.database.DBHelper
import com.vinade.todorooms.model.Card
import com.vinade.todorooms.model.Room
import com.vinade.todorooms.model.Task
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var  roomAdapter: RoomAdapter
    private lateinit var db : DataBase
    var myRooms = mutableListOf<String>()
    private var dbSQLite : DBHelper = DBHelper(this, null)

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this,
        R.anim.rotate_open_room_btn
    ) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this,
        R.anim.rotate_close_room_btn
    ) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this,
        R.anim.from_bottom_anim
    ) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this,
        R.anim.to_bottom_anim
    ) }

    private var clicked = false




    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        db = DataBase()
        db.initDatabase()

        title = "Rooms"
        val cursor = dbSQLite.getRoom()
        while (cursor!!.moveToNext()){
            Log.d("tag", "ID: " + cursor.getString(cursor.getColumnIndex(DBHelper.ID_ROOM)))
            myRooms.add(cursor.getString(cursor.getColumnIndex(DBHelper.ID_ROOM)))
        }
        cursor.close()
        db.initMyRooms(myRooms)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(applicationContext,2)
        roomAdapter = RoomAdapter(this)
        db.readAllRooms(roomAdapter, recyclerView)







        floatingBtn.setOnClickListener {
            onAddButtonClicked()
        }
        floatingBtnKey.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_for_key, null)
            alertDialogBuilder.setView(dialogLayout)
            val edit_text = dialogLayout.findViewById<EditText>(R.id.edit_text_search_room_id)
            val btn_add = dialogLayout.findViewById<Button>(R.id.search_room_id)

            val dialog = alertDialogBuilder.create()

            dialog.show()
            edit_text.requestFocus()
            btn_add.setOnClickListener {
                for(room in myRooms){
                    if(room == edit_text.text.toString()){
                        Toast.makeText(this, "Room already exist", Toast.LENGTH_LONG).show()
                    }
                }

                for (room in db.getListOfRoomsId()){
                    if(room == edit_text.text.toString()){
                        db.addMyRoom(room)
                        dbSQLite.addRoom(room)
                        roomAdapter = RoomAdapter(this)
                        db.readAllRooms(roomAdapter, recyclerView)
                        dialog.dismiss()
                        break
                    }
                }
                Toast.makeText(this, "This room doesn't exist", Toast.LENGTH_LONG).show()
            }


        }
        floatingBtnAddNew.setOnClickListener {
        showBottomSheet()
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

        val btnCreate = view.findViewById<Button>(R.id.btnCreate)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        btnCreate.setOnClickListener {
            val name = name.text.toString()
            val description = description.text.toString()

            val card = mutableListOf<Card>()
            val testTask = Task("TEST")
            testTask.initId()
            val task: MutableList<Task> = ArrayList()
            task.add(testTask)
            val room = Room(name,description, card, task)
            val roomID = room.initId()
            dbSQLite.addRoom(roomID)
            db.addMyRoom(roomID)
            db.writeNewRoom(room)
            dialog.dismiss()
//            val intent = Intent(this, RoomActivity::class.java)
//            intent.putExtra("roomID",roomID)
//            intent.putExtra("roomTitle",room.name)
//            startActivity(intent)

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