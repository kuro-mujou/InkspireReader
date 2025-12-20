package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.database.dao.ImagePathDao
import com.inkspire.ebookreader.data.database.model.ImagePathEntity
import com.inkspire.ebookreader.data.mapper.toDataClass
import com.inkspire.ebookreader.domain.model.ImagePath
import com.inkspire.ebookreader.domain.repository.ImagePathRepository

class ImagePathRepositoryImpl(
    private val imageDao: ImagePathDao
) : ImagePathRepository {
    override suspend fun getImagePathsByBookIds(bookId: List<String>): List<ImagePath> {
        return imageDao.getImagePathsByBookId(bookId).map {
            it.toDataClass()
        }
    }

    override suspend fun deleteByBookIds(bookId: List<String>) {
        imageDao.deleteByBookId(bookId)
    }

    override suspend fun saveImagePath(bookID: String, coverImagePath: List<String>) {
        val imagePathEntity = coverImagePath.map {
            ImagePathEntity(bookId = bookID, imagePath = it)
        }
        imageDao.saveImagePath(imagePathEntity)
    }

    override suspend fun deleteImagePathByPath(imagePath: String): Int {
        return imageDao.deleteImagePathByPath(imagePath)
    }
}