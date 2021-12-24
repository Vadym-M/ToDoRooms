package com.vinade.todorooms

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

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

            override fun onDataChange(snapshot: DataSnapshot) {
                arrayData.clear()
                for (objSnapshot in snapshot.getChildren()) {
                    val card = objSnapshot.getValue<Card>(Card::class.java)
                    arrayData.add(card!!)
                }
                recycler.layoutManager = LinearLayoutManager(context)
                recycler.adapter = context?.let { CardAdapter(arrayData, it) }
            }
        })
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
}