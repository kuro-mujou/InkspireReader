package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.domain.model.ImagePath

interface ImagePathRepository {
    suspend fun getImagePathsByBookIds(bookId: List<String>): List<ImagePath>
    suspend fun deleteByBookIds(bookId: List<String>)
    suspend fun saveImagePath(bookID: String, coverImagePath: List<String>)
    suspend fun deleteImagePathByPath(imagePath: String): Int
}