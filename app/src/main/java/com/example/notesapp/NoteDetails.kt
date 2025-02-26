package com.example.notesapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteViewModel
import com.example.notesapp.data.ViewModelFactory

class NoteDetails : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var reviewText: TextView
    private lateinit var noteImageView: ImageView
    private lateinit var backButton: ImageButton
    private lateinit var editButton: ImageButton
    private lateinit var deleteButton: ImageButton

    private val noteViewModel: NoteViewModel by viewModels {
        ViewModelFactory(application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        init()
        setupListeners()
        displayNoteDetails()
    }

    private fun init() {
        titleText = findViewById(R.id.titleText)
        reviewText = findViewById(R.id.reviewText)
        noteImageView = findViewById(R.id.noteImageView)
        backButton = findViewById(R.id.backButton)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }

        editButton.setOnClickListener {
            val intent = Intent(this, AddNote::class.java).apply {
                putExtra("note_id", intent.getIntExtra("note_id", -1))
            }
            startActivity(intent)
            finish()
        }

        deleteButton.setOnClickListener {
            val noteId = intent.getIntExtra("note_id", -1)
            if (noteId != -1) {
                noteViewModel.delete(Note(id = noteId, title = "", content = ""))
            }
            finish()
        }
    }

    private fun displayNoteDetails() {
        val noteId = intent.getIntExtra("note_id", -1)

        if (noteId != -1) {
            noteViewModel.getNoteById(noteId).observe(this) { note ->
                note?.let {
                    titleText.text = it.title
                    reviewText.text = it.content
//                    noteImageView.setImageURI(Uri.parse(it.imageUri))

                    if (!it.imageUri.isNullOrEmpty()) {
                        try {
                            noteImageView.setImageURI(Uri.parse(it.imageUri))
                            noteImageView.visibility = View.VISIBLE
                        } catch (e: Exception) {
                            noteImageView.visibility = View.GONE
                        }
                    } else {
                        noteImageView.visibility = View.GONE
                    }
                }
            }
        } else {

            titleText.text = intent.getStringExtra("note_title")
            reviewText.text = intent.getStringExtra("note_content")
        }
    }
}