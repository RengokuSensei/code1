# EBookReader

EBookReader is a feature-rich Android application for reading and interacting with EPUB and PDF files. It provides a seamless reading experience with functionalities for highlighting text, taking notes, and visualizing notes as a mind map.

## Features

*   **Dual Format Support**: Open and read both EPUB and PDF files.
*   **Recent Books**: Your recently opened books are saved for quick access.
*   **File Picker**: Browse and open ebooks from your device's storage.
*   **EPUB Highlighting**: Highlight important sections in your EPUBs.
*   **Note-Taking**: Add notes to any book.
*   **Mind Maps**: Generate a mind map from your notes to visualize key concepts.
*   **PDF Annotation**: Add annotations directly onto your PDF documents.

## Setup

To build and run this project, you'll need Android Studio.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/ebook-reader.git
    ```
2.  **Open in Android Studio:**
    Open Android Studio, select 'Open an existing Android Studio project', and navigate to the cloned repository folder.
3.  **Build the project:**
    Android Studio should automatically sync the Gradle project. If not, you can manually trigger a sync by going to `File > Sync Project with Gradle Files`.
4.  **Run the application:**
    You can run the application on an Android emulator or a physical device. Select your desired run configuration and click the 'Run' button.

## Usage

### Opening a Book

You can open a book in two ways:

1.  **From Assets**: The application comes with pre-packaged books in the `assets` folder. These will be displayed on the main screen when you first launch the app.
2.  **From Device Storage**: Click the 'Browse' button to open a file picker and select an `.epub` or `.pdf` file from your device.

### Reading and Interaction

*   **EPUBs**: When you open an EPUB file, you can highlight text by long-pressing and selecting the desired text.
*   **PDFs**: For PDF files, you can enable 'Annotation Mode' to add text annotations by tapping on the document.

### Notes and Mind Maps

For any book, you can:

*   **View/Add Notes**: Click the 'Notes' button next to a book on the main screen to view existing notes or add new ones.
*   **Summarize Notes**: In the notes view, you can see a summary of all your notes.
*   **Generate Mind Map**: Also in the notes view, you can generate a visual mind map from your notes.
