package com.inkspire.ebookreader.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "hidden_texts",
)
data class HiddenTextEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val textToHide: String,
    val createdTime: Long = System.currentTimeMillis()
)