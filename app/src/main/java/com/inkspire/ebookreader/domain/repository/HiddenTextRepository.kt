package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.domain.model.HiddenText
import kotlinx.coroutines.flow.Flow

interface HiddenTextRepository  {
    fun getHiddenTextsFlow(): Flow<List<HiddenText>>
    suspend fun getHiddenTexts(): List<HiddenText>
    suspend fun addHiddenText(textToHide: String)
    suspend fun deleteHiddenTexts(ids: List<Int>)
}