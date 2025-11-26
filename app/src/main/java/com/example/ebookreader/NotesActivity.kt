package com.example.ebookreader

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader

class NotesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private var bookTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        bookTitle = intent.getStringExtra("book_title")

        recyclerView = findViewById(R.id.notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notesAdapter = NotesAdapter(emptyList())
        recyclerView.adapter = notesAdapter

        findViewById<Button>(R.id.add_note_button).setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("book_title", bookTitle)
            startActivity(intent)
        }

        findViewById<Button>(R.id.summarize_notes_button).setOnClickListener {
            val notes = notesAdapter.getNotes()
            val summary = notes.joinToString("\n") { it.text }
            AlertDialog.Builder(this)
                .setTitle(R.string.note_summary)
                .setMessage(summary)
                .setPositiveButton(R.string.ok, null)
                .show()
        }

        findViewById<Button>(R.id.view_mind_map_button).setOnClickListener {
            val intent = Intent(this, MindMapActivity::class.java)
            intent.putExtra("book_title", bookTitle)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        if (bookTitle != null) {
            val notes = loadNotesFromFile(bookTitle!!)
            notesAdapter.updateNotes(notes)
        }
    }

    private fun loadNotesFromFile(bookTitle: String): List<Note> {
        try {
            val file = File(filesDir, "${Utils.sha256(bookTitle)}_notes.json")
            if (file.exists()) {
                val reader = FileReader(file)
                val type = object : TypeToken<List<Note>>() {}.type
                return Gson().fromJson(reader, type)
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_loading_notes, Toast.LENGTH_SHORT).show()
        }
        return emptyList()
    }
}
