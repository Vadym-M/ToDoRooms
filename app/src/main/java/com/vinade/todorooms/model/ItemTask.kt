package com.vinade.todorooms.model

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import java.util.*

class ItemTask : DatabaseReference.CompletionListener {
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
    var isDone: Boolean = false
        get() {
            return field
        }
    constructor(text: String){
        this.text = text
    }
    constructor()

    override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {

    }

}