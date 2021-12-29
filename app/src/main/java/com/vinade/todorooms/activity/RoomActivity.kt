package com.vinade.todorooms.activity

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.vinade.todorooms.fragment.CardFragment
import com.vinade.todorooms.R
import com.vinade.todorooms.fragment.TaskFragment
import com.vinade.todorooms.adapter.ViewPagerAdapter

class RoomActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager
    private lateinit var tab: TabLayout
    private lateinit var roomID: String
    private lateinit var roomTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        roomID = intent.getStringExtra("roomID").toString()
        roomTitle = intent.getStringExtra("roomTitle").toString()
        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)

        title = roomTitle


        val adapter = ViewPagerAdapter(supportFragmentManager)


        adapter.addFragment(TaskFragment(), "Tasks")
        adapter.addFragment(CardFragment(), "Cards")



        pager.adapter = adapter


        tab.setupWithViewPager(pager)
    }
    fun getRoomId():String{
        return roomID
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