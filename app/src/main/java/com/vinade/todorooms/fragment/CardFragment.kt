package com.vinade.todorooms.fragment

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.vinade.todorooms.database.DataBase
import com.vinade.todorooms.R
import com.vinade.todorooms.SwipeToDeleteCallBack
import com.vinade.todorooms.activity.CreateCardActivity
import com.vinade.todorooms.activity.MainActivity
import com.vinade.todorooms.activity.RoomActivity
import com.vinade.todorooms.adapter.CardAdapter
import com.vinade.todorooms.model.Card
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recycler: RecyclerView
    private var db: DataBase = DataBase()
    val arrayData = arrayListOf<Card>()
    lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_card, container, false)
        recycler = view.findViewById<RecyclerView>(R.id.recyclerOfCard)
        val fab = view.findViewById<FloatingActionButton>(R.id.card_fab)
        db.initDatabase()

        getCards() // show all cards

        fab.setOnClickListener {
            intentToCreateActivity(fab)
        }
        return view
    }
    fun getCards(){
        db.getReference().child("Rooms").child(getRoomId()).child("cards").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
                TODO("not implemented")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayData.clear()
                for (objSnapshot in snapshot.getChildren()) {
                    val card = objSnapshot.getValue<Card>(Card::class.java)
                    arrayData.add(card!!)
                }

                arrayData.sortWith(compareBy ({dateTimeStrToLocalDateTime(it.dateTime).year},
                    {dateTimeStrToLocalDateTime(it.dateTime).month},
                    {dateTimeStrToLocalDateTime(it.dateTime).dayOfWeek},
                    {dateTimeStrToLocalDateTime(it.dateTime).hour},
                    {dateTimeStrToLocalDateTime(it.dateTime).minute}))

                arrayData.reverse()
                adapter = CardAdapter(arrayData, context!!, getRoomId(), this@CardFragment)
                recycler.layoutManager = LinearLayoutManager(context)
                recycler.adapter = context?.let { adapter }
                var itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallBack(adapter))
                itemTouchHelper.attachToRecyclerView(recycler)
            }
        })


    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateTimeStrToLocalDateTime(str: String):LocalDateTime {
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy"))
    }

    fun intentToCreateActivity(fab: FloatingActionButton){
        val activity = activity as RoomActivity
        val intent = Intent(context, CreateCardActivity::class.java)
        intent.putExtra("roomID", getRoomId())
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity, fab, fab.transitionName
        )
        startActivity(intent, options.toBundle())

    }
    fun intentToCreateActivityFromRecycler(container: CardView, title:String, text:String, cardID:String){
        val activity = activity as RoomActivity
        container.transitionName = "transition_floating_btn"
        val intent = Intent(context, CreateCardActivity::class.java)
        intent.putExtra("roomID", getRoomId())
        intent.putExtra("title", title)
        intent.putExtra("text", text)
        intent.putExtra("cardID", cardID)
        intent.putExtra("recycler", true)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity, container, container.transitionName
        )
        startActivity(intent, options.toBundle())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun getRoomId():String{
        val roomActivity = activity as RoomActivity
        val roomID = roomActivity.getRoomId()
        return  roomID
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu, menu)
//
//        val manager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val search = menu?.findItem(R.id.appSearchBar)
//        val searchView = search?.actionView as SearchView
//        searchView.queryHint = "Search"
//
//        //searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                searchView.clearFocus()
//                searchView.setQuery("",false)
//                searchView.onActionViewCollapsed()
//                Log.e("tag", "ok ")
//                return true
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.filter.filter(newText)
//                Log.e("tag", "ok 1")
//                return false
//            }
//        })
//
//        super.onCreateOptionsMenu(menu, inflater)
//    }
}