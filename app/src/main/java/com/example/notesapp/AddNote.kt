package com.example.notesapp

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteViewModel
import com.example.notesapp.data.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class AddNote : AppCompatActivity() {

    private lateinit var mContext: Context
    private lateinit var titleTextInputEditText: TextInputEditText
    private lateinit var noteInputEditText: TextInputEditText
    private lateinit var backButton: ImageButton
    private lateinit var saveButton: ImageButton

    private val noteViewModel: NoteViewModel by viewModels {
        ViewModelFactory(application)
    }

    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

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
    }

    private fun setupListeners() {
        saveButton.setOnClickListener { showSaveDialog() }
        backButton.setOnClickListener { finish() }
    }

    private fun loadNoteForEditing() {
        if (noteId != -1) {
            noteViewModel.getNoteById(noteId).observe(this) { note ->
                note?.let {
                    titleTextInputEditText.setText(it.title)
                    noteInputEditText.setText(it.content)
                }
            }
        }
    }

    private fun showSaveDialog() {
        val title = titleTextInputEditText.text.toString()
        val content = noteInputEditText.text.toString()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(mContext, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(mContext)
            .setTitle("Save Note")
            .setMessage("Do you want to save this note?")
            .setPositiveButton("Yes") { dialog, _ ->
                save()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showDeleteDialog() {
        if (noteId != -1) {
            AlertDialog.Builder(mContext)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun save() {
        val title = titleTextInputEditText.text.toString()
        val content = noteInputEditText.text.toString()

        val note = Note(
            id = if (noteId != -1) noteId else 0,
            title = title,
            content = content
        )

        if (noteId != -1) {
            noteViewModel.update(note)
        } else {
            noteViewModel.insert(note)
        }
        finish()
    }


}