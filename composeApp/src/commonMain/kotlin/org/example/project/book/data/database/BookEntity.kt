package org.example.project.book.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String,
    val languages: List<String>, //You see, room doesn't save List, room is good with primitive types -> either create a separate table for this..not good for  asmall app.. a better solution is to serialize the list in a json string and save it into db, StringListTypeConverter
    val authors: List<String>,
    val firstPublishYear: String?,
    val ratingsAverage: Double?,
    val ratingsCount: Int?,
    val numPagesMedian: Int?,
    val numEditions: Int
)