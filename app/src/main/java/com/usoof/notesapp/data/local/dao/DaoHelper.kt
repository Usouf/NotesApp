package com.usoof.notesapp.data.local.dao

import com.usoof.notesapp.data.local.entity.Note

interface DaoHelper {

    suspend fun getAllNotes(): List<Note>

    suspend fun getNoteById(id: Long): Note

    suspend fun insertNote(note: Note): Long

    suspend fun deleteNote(note: Note)

}