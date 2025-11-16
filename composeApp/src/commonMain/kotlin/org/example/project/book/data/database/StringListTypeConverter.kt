package org.example.project.book.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


// Workaround: BookEntity List<String> is not appreciated by room => Ser/Des to json string (if app is big we can create a new table)
object StringListTypeConverter {
    @TypeConverter //room
    fun fromString(value: String): List<String>{
        return Json.decodeFromString(value)
    }

    @TypeConverter //room
    fun fromList(list: List<String>): String{
        return Json.encodeToString(list)
    }
}