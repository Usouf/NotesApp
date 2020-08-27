package com.usoof.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.usoof.notesapp.data.dao.NoteDao
import com.usoof.notesapp.data.entity.Note
import javax.inject.Singleton

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false )
abstract class DatabaseService : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        @Volatile
        private var INSTANCE: DatabaseService? = null

        fun getDatabase(context: Context): DatabaseService {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseService::class.java,
                    "word_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }

}