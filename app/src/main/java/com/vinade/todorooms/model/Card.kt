package com.vinade.todorooms.model

data class Card(val id:String, val title:String, val text:String, val dateTime:String) {
    constructor():this("","","","")
}