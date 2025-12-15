package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.domain.model.ImagePath

interface ImagePathRepository {
    suspend fun getImagePathsByBookId(bookId: List<String>): List<ImagePath>
    suspend fun deleteByBookId(bookId: List<String>)
    suspend fun saveImagePath(bookID: String, coverImagePath: List<String>)
    suspend fun deleteImagePathByPath(imagePath: String): Int
}