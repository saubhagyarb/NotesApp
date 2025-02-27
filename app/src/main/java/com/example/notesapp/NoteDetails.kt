package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteViewModel
import com.example.notesapp.data.ViewModelFactory
import kotlin.math.log

class NoteDetails : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var reviewText: TextView
    private lateinit var backButton: ImageButton
    private lateinit var editButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var noteImageView: ImageView

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
        backButton = findViewById(R.id.backButton)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)
        noteImageView = findViewById(R.id.noteImageView)
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
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { dialog, _ ->
                val noteId = intent.getIntExtra("note_id", -1)
                if (noteId != -1) {
                    noteViewModel.delete(Note(id = noteId, title = "", content = ""))
                }
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun displayNoteDetails() {
        titleText.text = intent.getStringExtra("note_title")
        reviewText.text = intent.getStringExtra("note_content")

        val imageUriString = intent.getStringExtra("note_image_uri")
        Log.d("NoteDetails", "Image URI: $imageUriString")

        if (!imageUriString.isNullOrEmpty()) {
            val imageUri = imageUriString.toUri()
            noteImageView.setImageURI(imageUri)
            noteImageView.visibility = ImageView.VISIBLE
            Log.d("NoteDetails", "Setting image with URI: $imageUri")
        } else {
            noteImageView.visibility = ImageView.GONE
            Log.d("NoteDetails", "Image URI is empty or null")
        }
    }

}