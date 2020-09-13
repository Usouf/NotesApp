package com.usoof.notesapp.ui.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usoof.notesapp.data.local.entity.Note
import com.usoof.notesapp.data.repository.NotesRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class CreateNoteViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    companion object {
        const val TAG = "Create Note ViewModel"
    }

    private val insertedNoteId = MutableLiveData<Long>()
    private val noteDeleted = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d(TAG, "$exception")
    }

    fun insertNotes(note: Note) {

        viewModelScope.launch(exceptionHandler) {
            val insertedItems = notesRepository.insertNote(note)
            Log.d(TAG, "insertNotes: $insertedItems")
            insertedNoteId.postValue(insertedItems)
        }

    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(exceptionHandler) {
            notesRepository.deleteNote(note)
            noteDeleted.postValue(true)
        }
    }

    fun getInsertedNotes(): LiveData<Long> =
        insertedNoteId

    fun getIsDeleted(): LiveData<Boolean> =
        noteDeleted

}