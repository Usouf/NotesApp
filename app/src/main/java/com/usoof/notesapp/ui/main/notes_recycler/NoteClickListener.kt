package com.usoof.notesapp.ui.main.notes_recycler

import com.usoof.notesapp.data.local.entity.Note

interface NoteClickListener {
    fun onNoteClicked(note: Note, position: Int)
}