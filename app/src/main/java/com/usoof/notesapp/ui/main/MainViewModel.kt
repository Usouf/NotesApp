package com.usoof.notesapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usoof.notesapp.data.local.entity.Note
import com.usoof.notesapp.data.repository.NotesRepository
import kotlinx.coroutines.launch

class MainViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    companion object {
        const val TAG = "Main ViewModel"
    }

    private val notesList = MutableLiveData<List<Note>>()
    private val insertedNote = MutableLiveData<Note>()
    private val updatedNote = MutableLiveData<Note>()
    private val isNoteDeleted = MutableLiveData<Boolean>()

    init {
        fetchNotes()
    }

    private fun fetchNotes() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "fetchNotes: fetching notes")
                val response = notesRepository.getAllNotes()
                Log.d(TAG, "fetchNotes: $response")
                notesList.postValue(response)
            } catch (e: Exception) {
                Log.d(TAG, "fetchNotes: $e")
            }
        }
    }

    fun getResults(id: Long) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "getInsertedNote: getting note")
                val noteById = notesRepository.getNoteById(id)
                insertedNote.postValue(noteById)
            } catch (e: Exception) {
                Log.d(TAG, "getInsertedNote: $e")
            }
        }
    }

    fun getUpdatedResults(id: Long) {
        viewModelScope.launch {
            try {
                val noteById = notesRepository.getNoteById(id)
                updatedNote.postValue(noteById)
            } catch (e: Exception) {
                Log.d(TAG, "getUpdatedNote: $e")
            }
        }
    }

    fun isNoteDeleted(deleted: Boolean) {
        isNoteDeleted.postValue(deleted)
    }

    fun getInsertedNote(): LiveData<Note> =
        insertedNote

    fun getNotes(): LiveData<List<Note>> =
        notesList

    fun getUpdatedNote(): LiveData<Note> =
        updatedNote

    fun getIsNoteDeleted(): LiveData<Boolean> =
        isNoteDeleted
}