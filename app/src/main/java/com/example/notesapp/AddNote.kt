package com.example.notesapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteViewModel
import com.example.notesapp.data.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream

class AddNote : AppCompatActivity() {

    private lateinit var mContext: Context
    private lateinit var titleTextInputEditText: TextInputEditText
    private lateinit var noteInputEditText: TextInputEditText
    private lateinit var backButton: ImageButton
    private lateinit var saveButton: ImageButton
    private lateinit var addImageButton: ImageButton
    private lateinit var removeImageButton: ImageButton
    private lateinit var selectedImageView: ImageView


    private var selectedImageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            selectedImageUri = uri
            displaySelectedImage()
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private val noteViewModel: NoteViewModel by viewModels {
        ViewModelFactory(application)
    }

    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mContext = this
        noteId = intent.getIntExtra("note_id", -1)

        init()
        setupListeners()
        loadNoteForEditing()
    }

    private fun init() {
        titleTextInputEditText = findViewById(R.id.titleInput)
        noteInputEditText = findViewById(R.id.noteInput)
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        addImageButton = findViewById(R.id.addImageButton)
        selectedImageView = findViewById(R.id.selectedImageView)
        removeImageButton = findViewById(R.id.removeImageButton)
    }

    private fun setupListeners() {
        saveButton.setOnClickListener { showSaveDialog() }
        backButton.setOnClickListener { finish() }
        addImageButton.setOnClickListener { addImage() }
        removeImageButton.setOnClickListener { removeImage() }
    }

    private fun loadNoteForEditing() {
        if (noteId != -1) {
            noteViewModel.getNoteById(noteId).observe(this) { note ->
                note?.let {
                    titleTextInputEditText.setText(it.title)
                    noteInputEditText.setText(it.content)

                    if (!it.imageUri.isNullOrEmpty()) {
                        selectedImageUri = Uri.parse(it.imageUri)
                        displaySelectedImage()
                    }
                }
            }
        }
    }

    private fun displaySelectedImage() {
        selectedImageUri?.let {
            selectedImageView.setImageURI(it)
            selectedImageView.visibility = View.VISIBLE
            removeImageButton.visibility = View.VISIBLE
        }
    }

    private fun removeImage() {
        selectedImageUri?.let { uri ->
            // Delete the image file from private storage if it exists
            val deleted = deleteFromPrivateStorage(uri.toString())
            if (!deleted) {
                Log.e("RemoveImage", "Failed to delete image: $uri")
            }
            else Log.d("RemoveImage", "Image deleted successfully: $uri")
        }
        selectedImageUri = null
        selectedImageView.setImageURI(null)
        selectedImageView.visibility = View.GONE
        removeImageButton.visibility = View.GONE
    }


    private fun showSaveDialog() {
        val title = titleTextInputEditText.text.toString().trim()
        val content = noteInputEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(mContext, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(mContext)
            .setTitle("Save Note")
            .setMessage("Do you want to save this note?")
            .setPositiveButton("Yes") { dialog, _ ->
                save(title, content)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun save(title: String, content: String) {
        val imagePath = selectedImageUri?.let { saveImageToPrivateStorage(it) }

        val note = Note(
            id = if (noteId != -1) noteId else 0,
            title = title,
            content = content,
            imageUri = imagePath
        )

        if (noteId != -1) {
            noteViewModel.update(note)
        } else {
            noteViewModel.insert(note)
        }
        finish()
    }
    private fun saveImageToPrivateStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, "images/${System.currentTimeMillis()}.jpg")
            file.parentFile?.mkdirs()

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            Log.e("SaveImage", "Error saving image", e)
            null
        }
    }

    private fun deleteFromPrivateStorage(uriString: String): Boolean {
        return try {
            val uri = Uri.parse(uriString)
            val file = File(uri.path ?: return false)

            if (file.exists()) {
                file.delete()
            } else {
                Log.e("DeleteImage", "File not found: $uriString")
                false
            }
        } catch (e: Exception) {
            Log.e("DeleteImage", "Error deleting image", e)
            false
        }
    }

    private fun addImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}