package com.usoof.notesapp.ui.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.usoof.notesapp.databinding.LayoutAddUrlBinding
import com.usoof.notesapp.databinding.LayoutDeleteNoteBinding
import com.usoof.notesapp.ui.ViewModelFactory
import com.usoof.notesapp.ui.main.MainActivity
import kotlinx.coroutines.*
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Create Note Activity"
        const val NOTE_ID = "INSERTED NOTE ID"
        const val NOTE_ISDELETED = "ISDELETED"
        const val REQUEST_CODE_STORAGE_PERMISSION = 1
        const val REQUEST_CODE_SELECT_IMAGE = 2
    }

    private lateinit var binding: ActivityCreateNoteBinding

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d(CreateNoteViewModel.TAG, "$exception")
    }

    private lateinit var viewModel: CreateNoteViewModel

    private var selectedColor = "#333333"
    private var selectedImagePath = ""

    private lateinit var dialogAddUrl: AlertDialog
    private lateinit var dialogDeleteNote: AlertDialog

    private var availableNote: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupUI()

        if (intent.getBooleanExtra(MainActivity.EXTRA_CODE_ISVIEWORUPDATE, false)) {
            availableNote = intent.getParcelableExtra(MainActivity.EXTRA_CODE_NOTE)
            setViewOrUpdateNote()
        }

        setupViewModel()

        initColorPicker()
        setSubtitleIndicatorColor()

        setupObservers()

    }

    private fun setViewOrUpdateNote() {
        availableNote?.apply {
            binding.titleText.setText(title)
            binding.textDateTime.text = dateTime
            binding.subtitleText.setText(subtitle)
            binding.noteText.setText(note)

            if (imagePath.trim().isNotEmpty()) {
                Log.d(TAG, "setViewOrUpdateNote: image = $imagePath")
                binding.imageNote.setImageBitmap(BitmapFactory.decodeFile(imagePath))
                binding.imageNote.visibility = View.VISIBLE
                binding.imageNoteDelete.visibility = View.VISIBLE
                selectedImagePath = imagePath
            } else {
                Log.d(TAG, "setViewOrUpdateNote: image isEmpty")
            }

            if (webLink.trim().isNotEmpty()) {
                Log.d(TAG, "setViewOrUpdateNote: weblink = $webLink")
                binding.textWebUrl.text = webLink
                binding.layoutWebUrl.visibility = View.VISIBLE
            }


        }
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

        binding.imageNoteDelete.setOnClickListener {
            binding.imageNote.setImageBitmap(null)
            binding.imageNote.visibility = View.GONE
            binding.imageNoteDelete.visibility = View.GONE
            selectedImagePath = ""
        }

        binding.imageWebUrlDelete.setOnClickListener {
            binding.textWebUrl.text = ""
            binding.layoutWebUrl.visibility = View.GONE
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(DaoHelperImpl(DatabaseBuilder.getInstance(this)))
        ).get(CreateNoteViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getInsertedNotes().observe(this, Observer {
            val intent = Intent()
            intent.putExtra(NOTE_ID, it)
            setResult(RESULT_OK, intent)
            finish()
        })

        viewModel.getIsDeleted().observe(this, Observer {
            val intent = Intent()
            intent.putExtra(NOTE_ISDELETED, it)
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

        availableNote?.apply {
            note.id = id
        }

        note.title = binding.titleText.text.toString()
        note.subtitle = binding.subtitleText.text.toString()
        note.note = binding.noteText.text.toString()
        note.dateTime = binding.textDateTime.text.toString()
        note.color = selectedColor
        Log.d(TAG, "getNote: $selectedImagePath")
        note.imagePath = selectedImagePath

        if (binding.layoutWebUrl.visibility == View.VISIBLE) {
            note.webLink = binding.textWebUrl.text.toString()
        }

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

        availableNote?.apply {
            if (color.trim().isNotEmpty()) {
                Log.d(TAG, "setViewOrUpdateNote: color = $color")
                when (color) {
                    "#FDBE3B" -> binding.layoutColorPickerContainer.viewColor2.performClick()
                    "#FF4842" -> binding.layoutColorPickerContainer.viewColor3.performClick()
                    "#3A52FC" -> binding.layoutColorPickerContainer.viewColor4.performClick()
                    "#000000" -> binding.layoutColorPickerContainer.viewColor5.performClick()
                }
            }
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

        binding.layoutColorPickerContainer.layoutAddUrl.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showAddUrlDialog()
        }

        availableNote?.let {
            binding.layoutColorPickerContainer.layoutRemoveNote.visibility = View.VISIBLE
            binding.layoutColorPickerContainer.layoutRemoveNote.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                showDeleteNoteDialog()
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

            selectedImagePath = getImagePathFromUri(selectedImageUri)
            Log.d(TAG, "onActivityResult: $selectedImagePath")

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
            binding.imageNoteDelete.visibility = View.VISIBLE
        }
    }

    private fun getImagePathFromUri(selectedImage: Uri?): String {
        var localImagePath = selectedImage?.path
        selectedImage?.let { imageUri ->
            val cursor = contentResolver.query(imageUri, null, null, null, null)
            cursor?.use { c ->
                c.moveToFirst()
                localImagePath = cursor.getString(c.getColumnIndex("_data"))
                c.close()
            }
        }

        Log.d(TAG, "getImagePathFromUri: $localImagePath")

        return localImagePath!!
    }

    private fun showAddUrlDialog() {

        val builder = AlertDialog.Builder(this)
        val addUrlBinding = LayoutAddUrlBinding.inflate(layoutInflater)
        val view = addUrlBinding.root

        builder.setView(view)

        dialogAddUrl = builder.create()

        if (dialogAddUrl.window != null) {
            dialogAddUrl.window?.setBackgroundDrawable(ColorDrawable(0))
        }

        addUrlBinding.editUrl.requestFocus()

        addUrlBinding.buttonDoneAddUrl.setOnClickListener {
            if (addUrlBinding.editUrl.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Enter URL", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.WEB_URL.matcher(addUrlBinding.editUrl.text.toString()).matches()) {
                Toast.makeText(this, "Enter a valid URL", Toast.LENGTH_SHORT).show()
            } else {
                binding.textWebUrl.text = addUrlBinding.editUrl.text.toString()
                binding.layoutWebUrl.visibility = View.VISIBLE
                dialogAddUrl.dismiss()
            }
        }

        addUrlBinding.buttonCancelAddUrl.setOnClickListener {
            dialogAddUrl.dismiss()
        }

        dialogAddUrl.show()
    }

    private fun showDeleteNoteDialog() {

        val builder = AlertDialog.Builder(this)
        val deleteNoteBinding = LayoutDeleteNoteBinding.inflate(layoutInflater)
        val view = deleteNoteBinding.root

        builder.setView(view)

        dialogDeleteNote = builder.create()

        if (dialogDeleteNote.window != null) {
            dialogDeleteNote.window?.setBackgroundDrawable(ColorDrawable(0))
        }

        deleteNoteBinding.buttonDelete.setOnClickListener {
            availableNote?.let {
                viewModel.deleteNote(it)
            }
        }

        deleteNoteBinding.buttonCancel.setOnClickListener {
            dialogDeleteNote.dismiss()
        }

        dialogDeleteNote.show()

    }

}