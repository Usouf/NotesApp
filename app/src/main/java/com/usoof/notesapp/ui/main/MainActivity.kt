package com.usoof.notesapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.usoof.notesapp.databinding.ActivityMainBinding
import com.usoof.notesapp.ui.add.CreateNoteActivity

class MainActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CODE_ADD_NOTE = 1
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imageOuterAdd.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
        }
    }
}