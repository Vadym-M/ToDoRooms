package com.vinade.todorooms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

class RoomActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager
    private lateinit var tab: TabLayout
    private lateinit var roomID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        roomID = intent.getStringExtra("roomID").toString()
        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)




        val adapter = ViewPagerAdapter(supportFragmentManager)


        adapter.addFragment(TaskFragment(), "Tasks")
        adapter.addFragment(CardFragment(), "Cards")



        pager.adapter = adapter


        tab.setupWithViewPager(pager)
    }
    fun getRoomId():String{
        return roomID
    }
    
}