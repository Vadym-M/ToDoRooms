package com.vinade.todorooms

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var layout: FrameLayout
    private lateinit var thisView: View
    private lateinit var floatingBtn: FloatingActionButton


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
        thisView = view
        layout = view.findViewById(R.id.layout_fragment_task)
        //
        floatingBtn = view.findViewById<FloatingActionButton>(R.id.btnCreateTask)
        showTasks(view)
        floatingBtn.setOnClickListener {
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

    fun removeItem(id:String, task:Task){
        Log.d("tag", "removeItem: count "+ arrayList.size)
        val array = arrayList
        for (item in array) {
             if(item.task.id == task.id){
                 Log.d("tag", "Jest taki id")
                 for(itm in item.task.items){
                     if(itm.id.equals(id)){
                         val db = DataBase()
                         db.initDatabase()
                         val index = item.task.items.indexOf(itm)

                         Snackbar.make(layout, "${itm.text} done!", Snackbar.LENGTH_LONG).addCallback(
                             object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                 override fun onShown(transientBottomBar: Snackbar?) {
                                     super.onShown(transientBottomBar)
                                     floatingBtn.visibility = View.GONE
                                 }

                                 override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                     super.onDismissed(transientBottomBar, event)
                                     floatingBtn.visibility = View.VISIBLE
                                 }
                             }
                         ).setAction("Return"){
                             item.task.items.add(index, itm)
                             db.updateItems(getRoomId(), item.task.id, item.task.items)
                         }.show()

                         item.task.items.remove(itm)
                         Log.d("tag", "item was romoved")

                         db.updateItems(getRoomId(), item.task.id, item.task.items)

                         val timer = object: CountDownTimer(3000, 1000) {
                             override fun onTick(millisUntilFinished: Long) {}

                             override fun onFinish() {

                             }
                         }
                         timer.start()
                         break
                     }
                 }
             }
        }

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
    fun getRootLayout(): FrameLayout{
        return layout
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




        ref.child("Rooms").child(getRoomId()).child("tasks").addValueEventListener(object : ValueEventListener {

            override fun onCancelled(snapshotError: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                listTask.clear()


                for (objSnapshot in snapshot.getChildren()) {
                    val task = objSnapshot.getValue<Task>(Task::class.java)
                    listTask.add(task!!)

                }
                listTask.sortBy { it.timestamp }
                listTask.reverse()
                for(item in listTask){

                    val adapter = TaskAdapter(item, context, this@TaskFragment)
                    adapter.initRoomID(getRoomId())
                    arrayList.add(adapter)
                }

                val concatAdapterConfig = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()
                val concatAdapter = ConcatAdapter(concatAdapterConfig, arrayList)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = concatAdapter

                for (arr in arrayList){
                    for (ex in expanded){
                        if(arr.task.id == ex){
                            arr.changeExpandFromLoop()
                            concatAdapter.notifyDataSetChanged()
                        }
                    }
                }


            }
        })
    }

    fun listenerDatabase(){

    }
    fun getRoomId():String{
        val roomID = roomActivity.getRoomId()
        return  roomID
    }
    fun initActivity(){
        roomActivity = activity as RoomActivity
    }

    override fun onPause() {
        super.onPause()
        context?.hideKeyboard(thisView)
    }
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}