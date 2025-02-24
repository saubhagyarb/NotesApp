package com.example.notesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.NoteViewModel
import com.example.notesapp.data.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class SearchActivity : AppCompatActivity() {

    private lateinit var mContext: Context
    private lateinit var searchBar: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private val noteViewModel: NoteViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mContext = this
        init()
        setupRecyclerView()
        setupSearchBar()
        observeNotes()
    }

    private fun init() {
        searchBar = findViewById(R.id.search_bar)
        recyclerView = findViewById(R.id.search_recycler_view)
    }

    private fun setupRecyclerView() {
        val adapter = NoteAdapter { note ->
            val intent = Intent(mContext, NoteDetails::class.java).apply {
                putExtra("note_id", note.id)
                putExtra("note_title", note.title)
                putExtra("note_content", note.content)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearchBar() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                noteViewModel.setSearchQuery(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeNotes() {
        noteViewModel.allNotes.observe(this) { notes ->
            (recyclerView.adapter as NoteAdapter).submitList(notes)
        }
    }
}