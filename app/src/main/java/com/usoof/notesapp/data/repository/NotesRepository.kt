package com.usoof.notesapp.data.repository

import com.usoof.notesapp.data.local.entity.Note

interface NotesRepository {

    suspend fun getAllNotes(): List<Note>

    suspend fun getNoteById(id: Long): Note

    suspend fun insertNote(note: Note): Long

    suspend fun deleteNote(note: Note)

}