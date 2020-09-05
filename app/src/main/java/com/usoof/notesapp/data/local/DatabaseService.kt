package com.usoof.notesapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.usoof.notesapp.data.local.dao.NoteDao
import com.usoof.notesapp.data.local.entity.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false )
abstract class DatabaseService : RoomDatabase() {

    abstract fun noteDao(): NoteDao

}