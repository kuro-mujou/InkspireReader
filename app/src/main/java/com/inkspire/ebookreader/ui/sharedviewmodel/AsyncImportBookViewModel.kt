package com.inkspire.ebookreader.ui.sharedviewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.inkspire.ebookreader.common.ScrapedBookInfo
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.worker.CBZImportWorker
import com.inkspire.ebookreader.worker.EPUBImportWorker
import com.inkspire.ebookreader.worker.PDFImportWorker
import com.inkspire.ebookreader.worker.TruyenFullImportWorker
import kotlinx.coroutines.launch

class AsyncImportBookViewModel: ViewModel() {

    fun processAndSaveBook(
        context: Context,
        filePath: String,
    ) = viewModelScope.launch {
        try {
            Toast.makeText(context, "Importing...", Toast.LENGTH_SHORT).show()
            val inputData = workDataOf(
                EPUBImportWorker.INPUT_URI_KEY to filePath,
            )
            val workRequest = OneTimeWorkRequest.Builder(EPUBImportWorker::class.java)
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Can't open ebook file", Toast.LENGTH_SHORT).show()
        }
    }

    fun processAndSavePdf(
        context: Context,
        filePath: String,
        fileName: String,
        specialIntent: String
    ) = viewModelScope.launch {
        try {
            Toast.makeText(context, "Importing...", Toast.LENGTH_SHORT).show()
            val inputData = Data.Builder()
                .putString(PDFImportWorker.INPUT_URI_KEY, filePath)
                .putString(PDFImportWorker.ORIGINAL_FILENAME_KEY, fileName)
                .putString(PDFImportWorker.SPECIAL_INTENT_KEY, specialIntent)
                .build()
            val workRequest = OneTimeWorkRequest.Builder(PDFImportWorker::class.java)
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Can't open PDF file", Toast.LENGTH_SHORT).show()
        }
    }

    fun processAndSaveCBZ(
        context: Context,
        filePath: String,
    ) = viewModelScope.launch {
        try {
            Toast.makeText(context, "Importing...", Toast.LENGTH_SHORT).show()
            val inputData = Data.Builder()
                .putString(CBZImportWorker.INPUT_URI_KEY, filePath)
                .build()
            val workRequest = OneTimeWorkRequest.Builder(CBZImportWorker::class.java)
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Can't open CBZ file", Toast.LENGTH_SHORT).show()
        }
    }

    fun processTruyenFullBook(
        context: Context,
        bookUrl: String,
        book: ScrapedBookInfo,
    ) = viewModelScope.launch {
        try {
            Toast.makeText(context, "Importing...", Toast.LENGTH_SHORT).show()
            val inputData = Data.Builder()
                .putString(TruyenFullImportWorker.INPUT_BOOK_URL_KEY, bookUrl)
                .putString(TruyenFullImportWorker.INPUT_BOOK_TITLE_KEY, book.title)
                .putString(TruyenFullImportWorker.INPUT_BOOK_AUTHOR_KEY, book.author)
                .putString(TruyenFullImportWorker.INPUT_BOOK_DESCRIPTION_KEY, book.descriptionHtml)
                .putString(TruyenFullImportWorker.INPUT_BOOK_CATEGORY_KEY, book.categories.joinToString(","))
                .putString(TruyenFullImportWorker.INPUT_BOOK_COVER_URL_KEY, book.coverUrl)
                .build()
            val workRequest = OneTimeWorkRequest.Builder(TruyenFullImportWorker::class.java)
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Can't download book, something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchNewChapter(
        context: Context,
        book: Book,
    ) = viewModelScope.launch {
        try {
            Toast.makeText(context, "Checking...", Toast.LENGTH_SHORT).show()
            val inputData = Data.Builder()
                .putString(TruyenFullImportWorker.INPUT_BOOK_URL_KEY, book.storagePath)
                .putString(TruyenFullImportWorker.INPUT_BOOK_TITLE_KEY, book.title)
                .build()
            val workRequest = OneTimeWorkRequest.Builder(TruyenFullImportWorker::class.java)
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Current chapter is latest", Toast.LENGTH_SHORT).show()
        }
    }
}