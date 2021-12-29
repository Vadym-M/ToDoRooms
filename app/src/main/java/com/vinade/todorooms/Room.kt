package com.vinade.todorooms

import java.util.*


class Room {
    var id: String = ""
        get() {
        return field
    }
    set(value) {
        field = value
    }

    var name: String = ""
        get() {
        return field
    }
    set(value) {
        field = value
    }
    var description: String = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }
    var password: String = ""
        get() {
            return field
        }
        set(value) {
            field = value
        }


    constructor()
    constructor(name: String, description: String, cards: MutableList<Card>, tasks: MutableList<Task>){

        this.name = name
        this.description = description
        this.password = password

    }
    fun initId():String{
        this.id = UUID.randomUUID().toString()
        return id
    }
    fun initDescription(){
        this.description = "None"
    }
}