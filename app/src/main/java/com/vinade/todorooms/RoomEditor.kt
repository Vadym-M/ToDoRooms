package com.vinade.todorooms

import android.util.Log
import com.vinade.todorooms.database.DataBase
import com.vinade.todorooms.model.Room

interface RoomEditor {
    fun editTitle(room: Room)
    fun editDescription(room: Room)



}