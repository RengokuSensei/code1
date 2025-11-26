package com.example.ebookreader

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.folioreader.FolioReader
import com.folioreader.model.HighLight
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    private val PREFS_NAME = "EBookReaderPrefs"
    private val RECENT_BOOKS_KEY = "RecentBooks"
    private val MAX_RECENT_BOOKS = 10

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var recentBooks: MutableList<Book>
    private lateinit var prefs: SharedPreferences

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                val book = Book(uri.toString())
                addRecentBook(book)
                val type = contentResolver.getType(uri)
                if (type == "application/epub+zip") {
                    val folioReader = FolioReader.get()
                    val highlights = loadHighlights(uri.toString())
                    folioReader.setHighlights(highlights)
                    folioReader.openBook(uri.toString())
                } else if (type == "application/pdf") {
                    val intent = Intent(this, PdfViewerActivity::class.java)
                    intent.putExtra("book_uri", uri.toString())
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        recentBooks = loadRecentBooks().toMutableList()

        recyclerView = findViewById(R.id.books_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val assetsBooks = assets.list("")?.filter { it.endsWith(".epub") || it.endsWith(".pdf") }
            ?.map { Book(it) } ?: emptyList()

        val allBooks = (recentBooks + assetsBooks).distinctBy { it.title }

        val folioReader = FolioReader.get()
        folioReader.setOnHighlightListener { highlight ->
            saveHighlight(highlight)
        }

        bookAdapter = BookAdapter(allBooks) { book, isNotesClick ->
            val bookId = if (book.title.startsWith("file://")) book.title else "file:///android_asset/${book.title}"
            if (isNotesClick) {
                val intent = Intent(this, NotesActivity::class.java)
                intent.putExtra("book_title", bookId)
                startActivity(intent)
            } else {
                if (book.title.endsWith(".epub")) {
                    val highlights = loadHighlights(bookId)
                    folioReader.setHighlights(highlights)
                    folioReader.openBook(bookId)
                } else if (book.title.endsWith(".pdf")) {
                    val intent = Intent(this, PdfViewerActivity::class.java)
                    intent.putExtra("book_title", book.title)
                    startActivity(intent)
                }
            }
        }
        recyclerView.adapter = bookAdapter

        findViewById<Button>(R.id.browse_button).setOnClickListener {
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        val mimeTypes = arrayOf("application/epub+zip", "application/pdf")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        filePickerLauncher.launch(intent)
    }

    private fun saveHighlight(highlight: HighLight) {
        val highlights = loadHighlights(highlight.bookId).toMutableList()
        highlights.add(highlight)
        val gson = Gson()
        val json = gson.toJson(highlights)
        try {
            val file = File(filesDir, "${Utils.sha256(highlight.bookId)}_highlights.json")
            val writer = FileWriter(file)
            writer.write(json)
            writer.close()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error saving highlights", e)
            Toast.makeText(this, R.string.error_saving_highlight, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadHighlights(bookId: String): List<HighLight> {
        try {
            val file = File(filesDir, "${Utils.sha256(bookId)}_highlights.json")
            if (file.exists()) {
                val reader = FileReader(file)
                val type = object : TypeToken<List<HighLight>>() {}.type
                return Gson().fromJson(reader, type)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading highlights", e)
            Toast.makeText(this, R.string.error_loading_highlights, Toast.LENGTH_SHORT).show()
        }
        return emptyList()
    }

    private fun saveRecentBooks(books: List<Book>) {
        val gson = Gson()
        val json = gson.toJson(books)
        prefs.edit().putString(RECENT_BOOKS_KEY, json).apply()
    }

    private fun loadRecentBooks(): List<Book> {
        val gson = Gson()
        val json = prefs.getString(RECENT_BOOKS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Book>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun addRecentBook(book: Book) {
        if (!recentBooks.contains(book)) {
            recentBooks.add(0, book)
            if (recentBooks.size > MAX_RECENT_BOOKS) {
                recentBooks.removeAt(recentBooks.size - 1)
            }
            saveRecentBooks(recentBooks)
        }
    }
}
