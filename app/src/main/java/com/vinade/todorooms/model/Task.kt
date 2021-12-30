package com.vinade.todorooms.model

import com.vinade.todorooms.model.ItemTask
import java.util.*
import kotlin.collections.ArrayList

class Task {
    var id: String = ""
    get() {
        return field
    }
    set(value){
        field = value
    }
    var title: String = ""
        get() {
            return field
        }
        set(value){
            field = value
        }
    var items: ArrayList<ItemTask> = mutableListOf<ItemTask>() as ArrayList<ItemTask>
        get() {
            return field
        }
        set(value){
            field = value
        }
    var timestamp: String = ""
        get() {
            return field
        }
        set(value){
            field = value
        }

    constructor()
    constructor(title: String){
        this.title = title
    }
    constructor(title: String, items:MutableList<ItemTask> ){
        this.title = title
        this.items = items as ArrayList<ItemTask>
    }
    fun initId(){
        this.id = UUID.randomUUID().toString()
    }
    fun addItem(item: ItemTask){
        items.add(item)
    }
    fun deleteItem(item: ItemTask){
        items.remove(item)
    }
}