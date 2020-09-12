package com.usoof.notesapp.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.usoof.notesapp.data.local.DatabaseBuilder
import com.usoof.notesapp.data.local.dao.DaoHelperImpl
import com.usoof.notesapp.data.local.entity.Note
import com.usoof.notesapp.databinding.ActivityMainBinding
import com.usoof.notesapp.ui.ViewModelFactory
import com.usoof.notesapp.ui.add.CreateNoteActivity
import com.usoof.notesapp.ui.main.notes_recycler.NoteClickListener
import com.usoof.notesapp.ui.main.notes_recycler.NotesAdapter

class MainActivity : AppCompatActivity(), NoteClickListener {

    companion object {
        const val REQUEST_CODE_ADD_NOTE = 1
        const val REQUEST_CODE_UPDATE_NOTE = 2
        const val EXTRA_CODE_ISVIEWORUPDATE = "IS VIEW OR UPDATE"
        const val EXTRA_CODE_NOTE = "NOTE"
        const val TAG = "Main Activity"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel

    private lateinit var notesAdapter: NotesAdapter
    private lateinit var noteList: ArrayList<Note>

    private var clickedNotePosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupUI()

        setupViewModel()

        setupObservers()

    }

    private fun setupUI() {
        binding.imageOuterAdd.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
        }

        noteList = ArrayList()
        notesAdapter = NotesAdapter(noteList, this)

        binding.notesRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = notesAdapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(DaoHelperImpl(DatabaseBuilder.getInstance(this)))
        ).get(MainViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getNotes().observe(this, Observer {
            Log.d(TAG, it.toString())
            Log.d(TAG, "${it.size}")
            notesAdapter.addAllNotes(it)
        })

        viewModel.getInsertedNote().observe(this, Observer {
            notesAdapter.addInsertedNote(it, 0)
            binding.notesRecyclerView.smoothScrollToPosition(0)
        })

        viewModel.getUpdatedNote().observe(this, Observer {
            clickedNotePosition?.let { position ->
                notesAdapter.addInsertedNote(it, position)
                binding.notesRecyclerView.smoothScrollToPosition(position)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: Result Received")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == Activity.RESULT_OK) {
            data?.getLongExtra(CreateNoteActivity.NOTE_ID, 0)?.run {
                viewModel.getResults(this)
            }
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == Activity.RESULT_OK) {
            data?.getLongExtra(CreateNoteActivity.NOTE_ID, 0)?.run {
                viewModel.getUpdatedResults(this)
            }
        }
    }

    override fun onNoteClicked(note: Note, position: Int) {
        clickedNotePosition = position
        val intent = Intent(applicationContext, CreateNoteActivity::class.java)
        intent.putExtra(EXTRA_CODE_ISVIEWORUPDATE, true)
        intent.putExtra(EXTRA_CODE_NOTE, note)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE)
    }
}