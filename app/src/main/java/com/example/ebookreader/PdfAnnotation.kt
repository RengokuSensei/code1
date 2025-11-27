package com.example.ebookreader

/**
 * Represents a single annotation on a PDF document.
 *
 * @property page The page number where the annotation is located.
 * @property x The x-coordinate of the annotation, as a fraction of the page width.
 * @property y The y-coordinate of the annotation, as a fraction of the page height.
 * @property text The text content of the annotation.
 */
data class PdfAnnotation(val page: Int, val x: Float, val y: Float, val text: String)
