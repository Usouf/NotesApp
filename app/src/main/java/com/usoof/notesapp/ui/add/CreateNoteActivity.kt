package com.usoof.notesapp.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.usoof.notesapp.databinding.ActivityCreateNoteBinding

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }
}