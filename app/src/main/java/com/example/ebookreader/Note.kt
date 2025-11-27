package com.example.ebookreader

/**
 * Represents a single note taken by the user for a book.
 *
 * @property text The content of the note.
 * @property timestamp The date and time when the note was created, in ISO 8601 format.
 */
data class Note(val text: String, val timestamp: String)
