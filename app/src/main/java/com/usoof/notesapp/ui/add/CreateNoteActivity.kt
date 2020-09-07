package com.usoof.notesapp.ui.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.usoof.notesapp.R
import com.usoof.notesapp.data.local.DatabaseBuilder
import com.usoof.notesapp.data.local.dao.DaoHelperImpl
import com.usoof.notesapp.data.local.entity.Note
import com.usoof.notesapp.databinding.ActivityCreateNoteBinding
import com.usoof.notesapp.ui.ViewModelFactory
import kotlinx.coroutines.*
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Create Note Activity"
        const val INSERTED_NOTE_ID = "Inserted Note ID"
        const val REQUEST_CODE_STORAGE_PERMISSION = 1
        const val REQUEST_CODE_SELECT_IMAGE = 2
    }

    private lateinit var binding: ActivityCreateNoteBinding

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d(CreateNoteViewModel.TAG, "$exception")
    }

    private lateinit var viewModel: CreateNoteViewModel

    private var selectedColor = "#333333"
    private var imagePath = ""

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
        Log.d(TAG, "getNote: $imagePath")
        note.imagePath = imagePath

        return note

    }

    private fun setSubtitleIndicatorColor() {
        val gradientDrawable = binding.subtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedColor))
    }

    private fun initColorPicker() {
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(binding.layoutColorPickerContainer.layoutColorPicker)
        binding.layoutColorPickerContainer.textColorPicker.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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


        binding.layoutColorPickerContainer.layoutAddImage.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                selectImage()
            }
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && permissions.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {

                GlobalScope.launch(exceptionHandler) {
                    Log.d(TAG, "onActivityResult: decoding started")
                    setImage(it)
                }

            }

            imagePath = getImagePathFromUri(selectedImageUri)
            Log.d(TAG, "onActivityResult: $imagePath")

        }
    }

    private suspend fun setImage(selectedImage: Uri) {
        var inputStream: InputStream? = null
        withContext(Dispatchers.IO) {
            inputStream = contentResolver.openInputStream(selectedImage)
        }
        var bitmap: Bitmap? = null
        withContext(Dispatchers.Default) {
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
        Log.d(TAG, "setImage: $bitmap")
        withContext(Dispatchers.Main) {
            binding.imageNote.setImageBitmap(bitmap)
            binding.imageNote.visibility = View.VISIBLE
        }
    }

    private fun getImagePathFromUri(selectedImage: Uri?) : String {
        var localImagePath = selectedImage?.path
        selectedImage?.let {imageUri ->
            val cursor = contentResolver.query(imageUri, null, null, null, null)
            cursor?.use {c ->
                c.moveToFirst()
                localImagePath = cursor.getString(c.getColumnIndex(MediaStore.Images.Media.DATA))
                c.close()
            }
        }

        Log.d(TAG, "getImagePathFromUri: $localImagePath")

        return localImagePath!!
    }
}