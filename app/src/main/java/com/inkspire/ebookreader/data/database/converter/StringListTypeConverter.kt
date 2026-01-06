package com.inkspire.ebookreader.data.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

object StringListTypeConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return Json.Default.decodeFromString(
            ListSerializer(String.Companion.serializer()), value
        )
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Json.Default.encodeToString(
            ListSerializer(String.serializer()), list
        )
    }
}