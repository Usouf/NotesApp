package com.usoof.notesapp.ui.add

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.usoof.notesapp.R
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

    private var selectedColor = "#333333"

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

        initColorPicker()
        setSubtitleIndicatorColor()

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

        binding.layoutColorPickerContainer.viewColor1.setOnClickListener {
            selectedColor = "#333333"
            binding.layoutColorPickerContainer.imageColor1.setImageResource(R.drawable.ic_done)
            binding.layoutColorPickerContainer.imageColor2.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor3.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor4.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        binding.layoutColorPickerContainer.viewColor2.setOnClickListener {
            selectedColor = "#FDBE3B"
            binding.layoutColorPickerContainer.imageColor1.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor2.setImageResource(R.drawable.ic_done)
            binding.layoutColorPickerContainer.imageColor3.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor4.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        binding.layoutColorPickerContainer.viewColor3.setOnClickListener {
            selectedColor = "#FF4842"
            binding.layoutColorPickerContainer.imageColor1.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor2.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor3.setImageResource(R.drawable.ic_done)
            binding.layoutColorPickerContainer.imageColor4.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        binding.layoutColorPickerContainer.viewColor4.setOnClickListener {
            selectedColor = "#3A52FC"
            binding.layoutColorPickerContainer.imageColor1.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor2.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor3.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor4.setImageResource(R.drawable.ic_done)
            binding.layoutColorPickerContainer.imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        binding.layoutColorPickerContainer.viewColor5.setOnClickListener {
            selectedColor = "#000000"
            binding.layoutColorPickerContainer.imageColor1.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor2.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor3.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor4.setImageResource(0)
            binding.layoutColorPickerContainer.imageColor5.setImageResource(R.drawable.ic_done)
            setSubtitleIndicatorColor()
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
        note.color = selectedColor

        return note

    }

    private fun setSubtitleIndicatorColor() {
        val gradientDrawable = binding.subtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedColor))
    }

    private fun initColorPicker() {
        val bottomSheetBehavior : BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(binding.layoutColorPickerContainer.layoutColorPicker)
        binding.layoutColorPickerContainer.textColorPicker.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
}