package com.example.ebookreader

import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader

/**
 * This activity displays a mind map generated from the notes of a specific book.
 * It uses a WebView to render the mind map using the markmap-autoloader.js library.
 */
class MindMapActivity : AppCompatActivity() {

    /**
     * Initializes the activity, sets up the WebView, and loads the mind map data.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_map)

        val bookTitle = intent.getStringExtra("book_title")
        val webView = findViewById<WebView>(R.id.mind_map_webview)

        if (bookTitle != null) {
            val notes = loadNotes(bookTitle)
            var notesHtml = ""
            for (note in notes) {
                notesHtml += "<li>${note.text}</li>"
            }

            val html = """
                <html>
                <head>
                    <title>Mind Map</title>
                    <script src="file:///android_asset/markmap-autoloader.js"></script>
                </head>
                <body>
                    <div class="markmap">
                    # $bookTitle
                    <ul>
                    $notesHtml
                    </ul>
                    </div>
                </body>
                </html>
            """.trimIndent()

            webView.settings.javaScriptEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
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
