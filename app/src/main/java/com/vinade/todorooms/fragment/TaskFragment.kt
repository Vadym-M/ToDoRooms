package com.vinade.todorooms.fragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionManager
import android.util.Log
import android.util.Pair as UtilPair
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.vinade.todorooms.database.DataBase
import com.vinade.todorooms.R
import com.vinade.todorooms.activity.ItemActivity
import com.vinade.todorooms.activity.RoomActivity
import com.vinade.todorooms.adapter.TaskAdapter
import com.vinade.todorooms.model.ItemTask
import com.vinade.todorooms.model.Task
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
    private lateinit var layout: RelativeLayout
    private lateinit var thisView: View
    private lateinit var floatingBtn: FloatingActionButton
    private lateinit var  createTaskTitle: EditText
    private lateinit var  nestedScroll: NestedScrollView
    private lateinit var  inputLayout: LinearLayout
    private lateinit var  transition_container: FrameLayout
    private lateinit var  emptyPattern: RelativeLayout
    var isKeyboard : Boolean = false


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

        initActivity()
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        thisView = view
        layout = view.findViewById(R.id.layout_fragment_task)
        floatingBtn = view.findViewById<FloatingActionButton>(R.id.btnCreateTask)
        showTasks(view)
        inputLayout = view.findViewById<LinearLayout>(R.id.input_layout)
        transition_container = view.findViewById<FrameLayout>(R.id.transition_container)
        nestedScroll = view.findViewById<NestedScrollView>(R.id.nestedScroll_container)
        val createTaskBtn = view.findViewById<Button>(R.id.new_task_btn)
        createTaskTitle = view.findViewById<EditText>(R.id.new_task_title)
        emptyPattern = view.findViewById<RelativeLayout>(R.id.empty_tasks)
        floatingBtn.setOnClickListener {


            showFields(createTaskBtn)
        }

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
    fun createTask(task: Task){
        val roomID = getRoomId()
    val db = DataBase()
        db.initDatabase()
        db.writeNewTask(task, roomID)
        db.readRoomById(roomID)
    }

    fun removeItem(id:String, task: Task){
        val array = arrayList
        for (item in array) {
             if(item.task.id == task.id){
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


                         db.updateItems(getRoomId(), item.task.id, item.task.items)

                         break
                     }
                 }
             }
        }

    }

    fun doneItem(id:String, task: Task){
        val array = arrayList
        for (item in array) {
            if(item.task.id == task.id){
                for(itm in item.task.items){
                    if(itm.id.equals(id)){
                        val db = DataBase()
                        db.initDatabase()
                        val index = item.task.items.indexOf(itm)
                        val isDone = item.task.items[index].isDone
                        item.task.items[index].isDone = !isDone


                        db.updateItems(getRoomId(), item.task.id, item.task.items)

                        break
                    }
                }
            }
        }

    }


    private fun showFields(createBtn: Button) {

        Handler(Looper.getMainLooper()).postDelayed({
            keyboardManager(createTaskTitle, true)
        }, 800)

        val firstTransform = transform(floatingBtn, inputLayout)

        val params = nestedScroll.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ABOVE, R.id.transition_container)
        nestedScroll.layoutParams = params
        TransitionManager.beginDelayedTransition(transition_container,firstTransform)
        floatingBtn.visibility = View.GONE
        inputLayout.visibility = View.VISIBLE


        createBtn.setOnClickListener {
            keyboardManager(createTaskTitle, false)

            val task = Task(createTaskTitle.text.toString())
            task.initId()
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            task.timestamp = currentDate
            createTask(task)

            Handler(Looper.getMainLooper()).postDelayed({
                createTaskTitle.text.clear()

                params.addRule(RelativeLayout.ABOVE, R.id.layout_fragment_task)
                nestedScroll.layoutParams = params

                val secondTranform = transform(inputLayout, floatingBtn)
                TransitionManager.beginDelayedTransition(transition_container,secondTranform)
                floatingBtn.visibility = View.VISIBLE
                inputLayout.visibility = View.GONE
            }, 100)

        }


    }
    fun hideFields(){
        createTaskTitle.text.clear()
        val params = nestedScroll.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ABOVE, R.id.layout_fragment_task)
        nestedScroll.layoutParams = params

        val secondTranform = transform(inputLayout, floatingBtn)
        TransitionManager.beginDelayedTransition(transition_container,secondTranform)
        floatingBtn.visibility = View.VISIBLE
        inputLayout.visibility = View.GONE
    }
    fun transform(start: View, end:View) : MaterialContainerTransform{
        return MaterialContainerTransform().apply {
            startView = start
            endView = end
            addTarget(endView)
            pathMotion = MaterialArcMotion()
            duration = 800
            setAllContainerColors(resources.getColor(R.color.light_gray))
            scrimColor = Color.TRANSPARENT
        }
    }
    fun keyboardManager(edit_text: EditText, show:Boolean) {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        val manager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(show){
            edit_text.requestFocus()
            manager.showSoftInput(edit_text, InputMethodManager.SHOW_FORCED)
            isKeyboard = true

        }else{
           manager.hideSoftInputFromWindow(edit_text.applicationWindowToken, 0)
            isKeyboard = false
        }

    }

    fun getRootLayout(): RelativeLayout{
        return layout
    }
    fun addExpand(itm:String){
        expanded.add(itm)
    }
    fun removeExpand(itm:String){
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
                    val fList = mutableListOf<ItemTask>()
                    val sList = mutableListOf<ItemTask>()
                    for(item in item.items){
                        if(item.isDone){
                            fList.add(item)
                        }else{
                            sList.add(item)
                        }
                    }
                    val concatList = sList + fList
                    item.items = concatList as ArrayList<ItemTask>
                    val adapter = TaskAdapter(item, context, this@TaskFragment)
                    adapter.initRoomID(getRoomId())
                    arrayList.add(adapter)
                }

                val concatAdapterConfig = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()
                val concatAdapter = ConcatAdapter(concatAdapterConfig, arrayList)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = concatAdapter

                if (concatAdapter.itemCount == 0){
                    Log.d("tag"," Concat size: "+ concatAdapter.itemCount)
                    emptyPattern.visibility = View.VISIBLE
                }else{
                    emptyPattern.visibility = View.GONE
                }

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

    fun intentToItemActivity(container: TextView, taskId:String, roomId:String){
        val activity = activity as RoomActivity
        val pair = UtilPair.create<View, String>(container, container.transitionName)
        //val pair2 = UtilPair.create<View, String>(container2, container2.transitionName)
        container.transitionName = "first_transition"
        val intent = Intent(context, ItemActivity::class.java).apply {
            putExtra("idTask", taskId)
            putExtra("idRoom", roomId) }
        val options = ActivityOptions.makeSceneTransitionAnimation(
            activity, container, "first_transition"
        )
        startActivity(intent, options.toBundle())
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
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    if(isKeyboard && inputLayout.visibility == View.VISIBLE){
                        hideFields()
                        keyboardManager(createTaskTitle, false)
                    }else{
                        activity?.finish()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

}