package com.usoof.notesapp.ui.add

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.usoof.notesapp.data.local.DatabaseBuilder
import com.usoof.notesapp.data.local.dao.DaoHelperImpl
import com.usoof.notesapp.data.local.entity.Note
import com.usoof.notesapp.databinding.ActivityCreateNoteBinding
import com.usoof.notesapp.ui.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Create Note Activity"
        const val INSERTED_NOTE_ID = "Inserted Note ID"
    }

    private lateinit var binding: ActivityCreateNoteBinding

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private lateinit var viewModel: CreateNoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupUI()

        setupViewModel()

        setupObservers()

    }

    private fun setupUI() {

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }

        binding.textDateTime.text =
            SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())

        binding.imageDone.setOnClickListener {
            val note: Note? = getNote()

            Log.d(TAG, "setupUI: $note : ${note?.id}")

            if (note != null) {
                viewModel.insertNotes(note)
            }

        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(DaoHelperImpl(DatabaseBuilder.getInstance(this)))
        ).get(CreateNoteViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getNotes().observe(this, Observer {
            val intent = Intent()
            intent.putExtra(INSERTED_NOTE_ID, it)
            setResult(RESULT_OK, intent)
            finish()
        })
    }

    private fun getNote(): Note? {

        if (binding.titleText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title can't be empty", Toast.LENGTH_SHORT).show()
            return null
        } else if (binding.subtitleText.text.toString().trim().isEmpty()
            && binding.noteText.text.toString().trim().isEmpty()
        ) {
            Toast.makeText(this, "Note text can't be empty", Toast.LENGTH_SHORT).show()
            return null
        }

        val note = Note()

        note.title = binding.titleText.text.toString()
        note.subtitle = binding.subtitleText.text.toString()
        note.note = binding.noteText.text.toString()
        note.dateTime = binding.textDateTime.text.toString()

        return note

    }
}