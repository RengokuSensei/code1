package com.example.ebookreader

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnDrawListener
import com.github.barteksc.pdfviewer.listener.OnTapListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class PdfViewerActivity : AppCompatActivity(), OnDrawListener, OnTapListener {

    private lateinit var pdfView: PDFView
    private var annotations = mutableListOf<PdfAnnotation>()
    private var bookUriString: String? = null
    private var bookTitle: String? = null
    private var annotationMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        pdfView = findViewById(R.id.pdfView)

        bookUriString = intent.getStringExtra("book_uri")
        bookTitle = intent.getStringExtra("book_title")

        if (bookUriString != null) {
            annotations = loadAnnotations(bookUriString!!).toMutableList()
            pdfView.fromUri(Uri.parse(bookUriString))
                .onDraw(this)
                .onTap(this)
                .load()
        } else if (bookTitle != null) {
            annotations = loadAnnotations(bookTitle!!).toMutableList()
            pdfView.fromAsset(bookTitle)
                .onDraw(this)
                .onTap(this)
                .load()
        }

        findViewById<Button>(R.id.annotate_button).setOnClickListener {
            annotationMode = !annotationMode
            if (annotationMode) {
                Toast.makeText(this, R.string.annotation_mode_enabled, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.annotation_mode_disabled, Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.view_notes_button).setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("book_title", if (bookUriString != null) bookUriString else bookTitle)
            startActivity(intent)
        }
    }

    override fun onLayerDrawn(canvas: Canvas?, pageWidth: Float, pageHeight: Float, displayedPage: Int) {
        val paint = Paint()
        paint.color = Color.RED
        paint.textSize = 20f
        for (annotation in annotations) {
            if (annotation.page == displayedPage) {
                canvas?.drawText(annotation.text, annotation.x * pageWidth, annotation.y * pageHeight, paint)
            }
        }
    }

    override fun onTap(event: android.view.MotionEvent?): Boolean {
        if (annotationMode && event != null) {
            showAnnotationDialog(event.x, event.y)
        }
        return true
    }

    private fun showAnnotationDialog(x: Float, y: Float) {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(R.string.add_annotation)
            .setView(editText)
            .setPositiveButton(R.string.add) { _, _ ->
                val text = editText.text.toString()
                if (text.isNotEmpty()) {
                    annotations.add(PdfAnnotation(pdfView.currentPage, x / pdfView.width, y / pdfView.height, text))
                    saveAnnotations()
                    pdfView.invalidate()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun saveAnnotations() {
        val gson = Gson()
        val json = gson.toJson(annotations)
        val fileName = getFileName()
        try {
            val file = File(filesDir, "${fileName}_annotations.json")
            val writer = FileWriter(file)
            writer.write(json)
            writer.close()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_saving_annotations, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAnnotations(fileName: String): List<PdfAnnotation> {
        try {
            val file = File(filesDir, "${getFileName()}_annotations.json")
            if (file.exists()) {
                val reader = FileReader(file)
                val type = object : TypeToken<List<PdfAnnotation>>() {}.type
                return Gson().fromJson(reader, type)
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_loading_annotations, Toast.LENGTH_SHORT).show()
        }
        return emptyList()
    }

    private fun getFileName(): String {
        val name = if (bookUriString != null) bookUriString else bookTitle
        return Utils.sha256(name!!)
    }
}
