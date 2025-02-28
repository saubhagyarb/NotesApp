package com.example.notesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.NoteViewModel
import com.example.notesapp.data.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var mContext: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var searchButton: ImageButton
    private lateinit var emptyStateLayout: TextView


    private val noteViewModel: NoteViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this

        init()
        setupRecyclerView()
        setupListeners()
        observeNotes()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = SpringItemAnimator()

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    recyclerView.invalidateItemDecorations()
                }
            })
        }
    }

    private fun init() {
        recyclerView = findViewById(R.id.recycler_view)
        addButton = findViewById(R.id.add_button)
        searchButton = findViewById(R.id.searchButton)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
    }

    private fun setupRecyclerView() {
        val adapter = NoteAdapter { note ->
            val intent = Intent(mContext, NoteDetails::class.java).apply {
                putExtra("note_id", note.id)
                putExtra("note_title", note.title)
                putExtra("note_content", note.content)
                putExtra("note_image_uri", note.imageUri)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(mContext)
    }

    private fun setupListeners() {
        addButton.setOnClickListener {
            startActivity(Intent(mContext, AddNote::class.java))
        }

        searchButton.setOnClickListener {
            startActivity(Intent(mContext, SearchActivity::class.java))
        }
    }

    private fun observeNotes() {
        noteViewModel.allNotes.observe(this) { notes ->
            val adapter = recyclerView.adapter as NoteAdapter
            adapter.submitList(notes)


            if (notes.isEmpty()) {
                emptyStateLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyStateLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
}