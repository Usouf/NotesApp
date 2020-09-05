package com.usoof.notesapp.data.local

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {

    private var INSTANCE: DatabaseService? = null

    fun getInstance(context: Context): DatabaseService {
        if (INSTANCE == null) {
            synchronized(DatabaseService::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context): DatabaseService =
        Room.databaseBuilder(
            context.applicationContext,
            DatabaseService::class.java,
            "word_database"
        ).build()

}







