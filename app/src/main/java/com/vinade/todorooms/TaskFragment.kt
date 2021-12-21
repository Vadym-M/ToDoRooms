package com.vinade.todorooms

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var roomActivity: RoomActivity
    val expanded = arrayListOf<String>()
    val arrayList = arrayListOf<TaskAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        initActivity()
        val view = inflater.inflate(R.layout.fragment_task, container, false)
        //my code
        val groupedItemList = arrayListOf<Task>()
        val listItems = arrayListOf<ItemTask>()
        listItems.add(ItemTask("one"))
        listItems.add(ItemTask("two"))
        listItems.add(ItemTask("three"))
        listItems.add(ItemTask("four"))
        listItems.add(ItemTask("five"))
        listItems.add(ItemTask("six"))

        val task = Task("Test", listItems)

        groupedItemList.add(task)


        //
        val btnCreate = view.findViewById<FloatingActionButton>(R.id.btnCreateTask)
        showTasks(view)
        btnCreate.setOnClickListener {
        //showdialog()
            showDialog()
        }


        //my code
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaskFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun createTask(task:Task){
        val roomID = getRoomId()
    val db = DataBase()
        db.initDatabase()
        db.writeNewTask(task, roomID)
        db.readRoomById(roomID)
    }


    private fun showDialog() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.custom_dialog)

        val yesBtn = dialog?.findViewById(R.id.dialog_ok_btn) as Button
        val noBtn = dialog.findViewById(R.id.dialog_cancel_btn) as Button
        val input = dialog.findViewById<EditText>(R.id.input_title_task)
        yesBtn.setOnClickListener {
            val task = Task(input.text.toString())
            task.initId()
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            task.timestamp = currentDate
            createTask(task)
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
    fun addExpand(itm:String){
        Log.d("tag", "addExpand")
        expanded.add(itm)
    }
    fun removeExpand(itm:String){
        Log.d("tag", "removeExpand")
        expanded.remove(itm)
    }
    fun showTasks(view: View){
        val database = DataBase()
        database.initDatabase()
        val ref = database.getReference()
        val listTask = arrayListOf<Task>()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerOfTask)




        //insertData(concatAdapter,arrayList )
        ref.child("Rooms").child(getRoomId()).child("tasks").addValueEventListener(object : ValueEventListener {

            override fun onCancelled(snapshotError: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                listTask.clear()


                for (objSnapshot in snapshot.getChildren()) {
                    val task = objSnapshot.getValue<Task>(Task::class.java)
                    listTask.add(task!!)

                }
                listTask.sortBy { it.timestamp }
                listTask.reverse()
                for(item in listTask){

                    val adapter = TaskAdapter(item, context, this@TaskFragment)
//                    if(!secondArrayList.isEmpty()){
//                        Log.d("tag", "IN LOOP")
//                        secondArrayList.forEach(){
//                            Log.d("tag", "ID of item: " + item.id + " And list id: "+ it.task.id)
//                            if(list.task.id == item.id){
//                                Log.d("tag", "IN LOOP: "+ list.isExpanded)
//                                adapter.isExpanded = list.isExpanded
//                            }
//                        }
//                    }
                    adapter.initRoomID(getRoomId())
                    arrayList.add(adapter)
                }

                val concatAdapterConfig = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()
                val concatAdapter = ConcatAdapter(concatAdapterConfig, arrayList)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = concatAdapter



                Log.d("tag", "Expand is: " + expanded.size + " and arralist: "+ arrayList.size)
                for (arr in arrayList){
                    for (ex in expanded){
                        if(arr.task.id == ex){
                            arr.changeExpandFromLoop()
                            concatAdapter.notifyDataSetChanged()
                        }
                    }
                }
                //expanded.clear()

//                arrayList.get(5).changeExpand()
//                concatAdapter.notifyDataSetChanged()
                arrayList.clear()
               // Log.d("tag", "EXPAND: " + arrayList.get(5).isExpanded)


//                    listTask.forEach {
//                        for (arr in oldArray) {
//                            if (!arr.task.id.equals(it.id)) {
//                                insertNewTask(concatAdapter, arrayList, it)
//                            }
//                        }
//                    }



//                ref.child("Rooms").child(getRoomId()).child("tasks").addValueEventListener(object : ValueEventListener {
//
//                    override fun onCancelled(snapshotError: DatabaseError) {
//                        TODO("not implemented")
//                    }
//
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val data = arrayListOf<Task>()
//                        Log.d("tag","SIZE : " + arrayList.size)
//
//
//                        for (objSnapshot in snapshot.getChildren()) {
//                            var task = objSnapshot.getValue<Task>(Task::class.java)
//                            if(task != null){
//                                var check = false
//                            for (arr in arrayList){
//                                Log.d("tag","Array : ")
//                                if(arr.task.id.equals(task.id)){
//                                    check = true
//                                }
//                            }
//                            if(!check){
//                                val array = TaskAdapter(task, context)
//                                arrayList.add(0,array)
//                                Log.d("tag","SECOND SIZE : " + arrayList.size + " And new task: "+ task.title)
//                                concatAdapter = ConcatAdapter(concatAdapterConfig,arrayList)
//                                recyclerView.layoutManager = LinearLayoutManager(context)
//                                recyclerView.adapter = concatAdapter
//                                check = false
//                                Log.d("tag","HEREUOS" +  "  task: ")
//                            }}
//                            data.add(task!!)
//
//                        }
//
//                        for (items in concatAdapter.adapters){
//
//                        }
//                    }})
                    }
        })







        val db = DataBase()
        db.initDatabase()

        //db.readAllTasksbyId(id, taskAdapter, recyclerView)
    }


    fun getRoomId():String{
        val roomID = roomActivity.getRoomId()
        return  roomID
    }
    fun initActivity(){
        roomActivity = activity as RoomActivity
    }

}