package com.vinade.todorooms.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Card(val id:String, val title:String, val text:String, val dateTime:String) {
    constructor():this("","","","")
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getDateTime(): LocalDateTime{
//        return LocalDateTime.parse(this.dateTime, DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy"));
//    }
}
