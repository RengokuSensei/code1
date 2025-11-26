package com.example.ebookreader

import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader

class MindMapActivity : AppCompatActivity() {

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
