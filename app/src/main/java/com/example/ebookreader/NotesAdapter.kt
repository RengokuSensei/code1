package com.example.ebookreader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * An adapter for displaying a list of notes in a RecyclerView.
 *
 * @param notes The initial list of notes to be displayed.
 */
class NotesAdapter(private var notes: List<Note>) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    /**
     * Updates the list of notes and notifies the adapter of the data change.
     *
     * @param newNotes The new list of notes to be displayed.
     */
    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    /**
     * Returns the current list of notes.
     *
     * @return The current list of notes.
     */
    fun getNotes(): List<Note> {
        return notes
    }

    /**
     * Creates a new ViewHolder for a note item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new NoteViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    /**
     * Binds the data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = notes.size

    /**
     * A ViewHolder for a note item in the RecyclerView.
     *
     * @param itemView The view for the note item.
     */
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val noteText: TextView = itemView.findViewById(R.id.note_text)
        private val noteTimestamp: TextView = itemView.findViewById(R.id.note_timestamp)

        /**
         * Binds a note to the ViewHolder.
         *
         * @param note The note to be displayed.
         */
        fun bind(note: Note) {
            noteText.text = note.text
            noteTimestamp.text = note.timestamp
        }
    }
}
