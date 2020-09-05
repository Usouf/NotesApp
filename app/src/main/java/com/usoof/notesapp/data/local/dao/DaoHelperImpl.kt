package com.usoof.notesapp.data.local.dao

import com.usoof.notesapp.data.local.DatabaseService
import com.usoof.notesapp.data.local.entity.Note

class DaoHelperImpl(private val databaseService: DatabaseService) : DaoHelper {

    override suspend fun getAllNotes(): List<Note> =
        databaseService.noteDao().getAllNotes()

    override suspend fun getNoteById(id: Long): Note =
        databaseService.noteDao().getNoteById(id)

    override suspend fun insertNote(note: Note): Long =
        databaseService.noteDao().insertNote(note)

    override suspend fun deleteNote(note: Note) =
        databaseService.noteDao().deleteNote(note)
}