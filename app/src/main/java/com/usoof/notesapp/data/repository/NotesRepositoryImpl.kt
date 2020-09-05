package com.usoof.notesapp.data.repository

import com.usoof.notesapp.data.local.dao.DaoHelper
import com.usoof.notesapp.data.local.entity.Note

class NotesRepositoryImpl(private val daoHelper: DaoHelper) : NotesRepository {

    override suspend fun getAllNotes(): List<Note> =
        daoHelper.getAllNotes()

    override suspend fun getNoteById(id: Long): Note =
        daoHelper.getNoteById(id)

    override suspend fun insertNote(note: Note): Long =
        daoHelper.insertNote(note)

    override suspend fun deleteNote(note: Note) =
        daoHelper.deleteNote(note)

}