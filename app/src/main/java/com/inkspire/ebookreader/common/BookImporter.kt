package com.inkspire.ebookreader.common

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.ui.sharedviewmodel.AsyncImportBookViewModel
import com.inkspire.ebookreader.util.FilePickerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class BookImporter(
    private val context: Context,
    private val scope: CoroutineScope,
    private val specialIntent: String,
) {
    val importBookViewModel: AsyncImportBookViewModel by inject(AsyncImportBookViewModel::class.java)
    fun processIntentUri(uri: Uri?) {
        uri?.let {
            val fileName = FilePickerUtil.getFileName(context, it)
            try {
                scope.launch {
                    when {
                        fileName.endsWith(".epub", ignoreCase = true) -> {
                            importBookViewModel.processAndSaveBook(context, it.toString())
                        }

                        fileName.endsWith(".pdf", ignoreCase = true) -> {
                            importBookViewModel.processAndSavePdf(context, it.toString(), fileName,specialIntent)
                        }

                        fileName.endsWith(".cbz", ignoreCase = true) -> {
                            importBookViewModel.processAndSaveCBZ(context, it.toString())
                        }

                        else -> {
                            Toast.makeText(context, "Unsupported file format", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } catch (_: Exception) {
                Toast.makeText(context, "Can't open book file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun importTruyenFullBook(boolUrl: String, book: ScrapedBookInfo) {
        scope.launch {
            importBookViewModel.processTruyenFullBook(context, boolUrl, book)
        }
    }

    fun fetchNewChapter(book: Book) {
        scope.launch {
            importBookViewModel.fetchNewChapter(context, book)
        }
    }
}