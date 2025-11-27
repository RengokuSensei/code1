package com.example.ebookreader

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * This activity allows the user to add a new note to a specific book.
 * It provides a simple interface with an EditText for the note text and a save button.
 */
class AddNoteActivity : AppCompatActivity() {

    private var bookTitle: String? = null

    /**
     * Initializes the activity, sets up the UI, and handles the save button click.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        bookTitle = intent.getStringExtra("book_title")
        val editText = findViewById<EditText>(R.id.note_edit_text)

        findViewById<Button>(R.id.save_note_button).setOnClickListener {
            val noteText = editText.text.toString()
            if (noteText.isNotEmpty() && bookTitle != null) {
                saveNote(bookTitle!!, noteText)
                finish()
            }
        }
    }

    /**
     * Saves a new note to a file associated with the book.
     *
     * @param bookTitle The title of the book to which the note is being added.
     * @param noteText The text of the note to be saved.
     */
    private fun saveNote(bookTitle: String, noteText: String) {
        val notes = loadNotes(bookTitle).toMutableList()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val newNote = Note(noteText, sdf.format(Date()))
        notes.add(newNote)

        val gson = Gson()
        val json = gson.toJson(notes)
        try {
            val file = File(filesDir, "${Utils.sha256(bookTitle)}_notes.json")
            val writer = FileWriter(file)
            writer.write(json)
            writer.close()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_saving_note, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Loads the list of notes for a specific book from a JSON file.
     *
     * @param bookTitle The title of the book for which to load the notes.
     * @return A list of Note objects. Returns an empty list if the file doesn't exist or an error occurs.
     */
    private fun loadNotes(bookTitle: String): List<Note> {
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
