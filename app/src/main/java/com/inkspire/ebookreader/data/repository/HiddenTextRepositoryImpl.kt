package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.database.dao.HiddenTextDao
import com.inkspire.ebookreader.data.database.model.HiddenTextEntity
import com.inkspire.ebookreader.data.mapper.toModel
import com.inkspire.ebookreader.domain.model.HiddenText
import com.inkspire.ebookreader.domain.repository.HiddenTextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HiddenTextRepositoryImpl(private val dao: HiddenTextDao) : HiddenTextRepository {
    override fun getHiddenTextsFlow(): Flow<List<HiddenText>> {
        return dao.getHiddenTextsFlow().map {
            it.map { entity ->
                entity.toModel()
            }
        }
    }

    override suspend fun getHiddenTexts(): List<HiddenText> {
        return dao.getHiddenTexts().map {
            it.toModel()
        }
    }

    override suspend fun addHiddenText(textToHide: String) {
        dao.insertHiddenText(HiddenTextEntity(textToHide = textToHide))
    }

    override suspend fun deleteHiddenTexts(ids: List<Int>) {
        dao.deleteHiddenTexts(ids)
    }
}