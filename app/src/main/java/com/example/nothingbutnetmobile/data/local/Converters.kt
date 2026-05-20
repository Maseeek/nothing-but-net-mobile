package com.example.nothingbutnetmobile.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromDoubleList(value: List<Double>?): String {
        return value?.let { "[${it.joinToString(",")}]" } ?: "[]"
    }

    @TypeConverter
    fun toDoubleList(value: String?): List<Double> {
        if (value.isNullOrBlank()) return emptyList()
        val cleaned = value.removeSurrounding("[", "]").trim()
        if (cleaned.isEmpty()) return emptyList()
        return cleaned.split(",").mapNotNull { it.trim().toDoubleOrNull() }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String {
        return value?.let { "[${it.joinToString(",")}]" } ?: "[]"
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int> {
        if (value.isNullOrBlank()) return emptyList()
        val cleaned = value.removeSurrounding("[", "]").trim()
        if (cleaned.isEmpty()) return emptyList()
        return cleaned.split(",").mapNotNull { it.trim().toIntOrNull() }
    }
}
