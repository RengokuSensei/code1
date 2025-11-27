package com.example.ebookreader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * An adapter for displaying a list of books in a RecyclerView.
 *
 * @param books The list of books to be displayed.
 * @param onItemClick A lambda function to be invoked when an item is clicked. The boolean parameter is true if the notes button is clicked, false otherwise.
 */
class BookAdapter(private val books: List<Book>, private val onItemClick: (Book, Boolean) -> Unit) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    /**
     * Creates a new ViewHolder for a book item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new BookViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    /**
     * Binds the data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
        holder.itemView.setOnClickListener { onItemClick(book, false) }
        holder.itemView.findViewById<Button>(R.id.notes_button).setOnClickListener {
            onItemClick(book, true)
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = books.size

    /**
     * A ViewHolder for a book item in the RecyclerView.
     *
     * @param itemView The view for the book item.
     */
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.book_title)

        /**
         * Binds a book to the ViewHolder.
         *
         * @param book The book to be displayed.
         */
        fun bind(book: Book) {
            titleTextView.text = book.title
        }
    }
}
