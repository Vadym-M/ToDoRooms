package com.vinade.todorooms

import java.util.*

class ItemTask {
    var id: String = UUID.randomUUID().toString()
        get() {
            return field
        }
        set(value) {
        field = value
    }
    var text: String = ""
        get() {
            return field
        }
    val isDone: Int = 0
        get() {
            return field
        }
    constructor(text: String){
        this.text = text
    }
    constructor()
}