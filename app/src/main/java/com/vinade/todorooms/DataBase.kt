package com.vinade.todorooms


import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DataBase {
    private lateinit var database: DatabaseReference
    val list = mutableListOf<Room>()
    get() = field
    fun initDatabase(){
        database = Firebase.database.reference
    }
    fun getReference():DatabaseReference{
        return database
    }

    fun writeNewRoom(room: Room){
        database.child("Rooms").child(room.id).setValue(room)
    }

    fun writeNewTask(task: Task, roomId : String){
        database.child("Rooms").child(roomId).child("tasks").child(task.id).setValue(task)
    }

    fun readAllRooms(roomAdapter: RoomAdapter, recyclerView: RecyclerView){

        database.child("Rooms").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (objSnapshot in snapshot.getChildren()) {
                    val myClass= objSnapshot.getValue<Room>(Room::class.java)
                    list.add(myClass!!)
                }
                roomAdapter.setRoomList(list)
                recyclerView.adapter = roomAdapter
            }
        })
    }

    fun readRoomById(id : String){
        database.child("Rooms").child(id).get().addOnSuccessListener {
            val item = it.getValue(Room::class.java)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
    fun updateItems(roomId: String, taskId: String, items: ArrayList<ItemTask>){
        database.child("Rooms").child(roomId).child("tasks").child(taskId).child("items").setValue(items)
    }
    fun updateTitleTask(roomId: String, task: Task, newTitle: String){
        database.child("Rooms").child(roomId).child("tasks").child(task.id).child("title").setValue(newTitle)
    }
    fun removeTask(roomId: String, task: Task){
        database.child("Rooms").child(roomId).child("tasks").child(task.id).removeValue()
    }
    fun removeAllItems(roomId: String, task: Task){
        database.child("Rooms").child(roomId).child("tasks").child(task.id).child("items").removeValue()
    }
    fun writeNewCard(roomId: String, card:Card){
        database.child("Rooms").child(roomId).child("cards").child(card.id).setValue(card)
    }
    fun updateCard(text:String, title:String, roomId: String, cardId:String){
        if(text != null){
            database.child("Rooms").child(roomId).child("cards").child(cardId).child("text").setValue(text)
        }
        if(title != null){
            database.child("Rooms").child(roomId).child("cards").child(cardId).child("title").setValue(title)
        }

    }
    fun removeCard(roomId: String, card:Card){
        database.child("Rooms").child(roomId).child("cards").child(card.id).removeValue()
    }
    fun getTaskById(taskId : String, roomId: String, ): Task{
        var task: Task = Task()
        database.child("Rooms").child(roomId).child("tasks").child(taskId).get().addOnSuccessListener {
            task = it.getValue(Task::class.java)!!
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        return task
    }




}


