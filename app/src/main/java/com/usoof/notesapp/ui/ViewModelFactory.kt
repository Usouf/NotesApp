package com.usoof.notesapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.usoof.notesapp.data.local.dao.DaoHelper
import com.usoof.notesapp.data.repository.NotesRepositoryImpl
import com.usoof.notesapp.ui.add.CreateNoteViewModel
import com.usoof.notesapp.ui.main.MainViewModel

class ViewModelFactory(private val daoHelper: DaoHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(NotesRepositoryImpl(daoHelper)) as T
        }
        if (modelClass.isAssignableFrom(CreateNoteViewModel::class.java)) {
            return CreateNoteViewModel(NotesRepositoryImpl(daoHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}