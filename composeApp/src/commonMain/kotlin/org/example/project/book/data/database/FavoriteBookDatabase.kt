package org.example.project.book.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters


@Database(
    entities = [BookEntity::class], //our database contains this table with header = BookEntity
    version =  1
)
@TypeConverters(
    StringListTypeConverter::class
)
abstract class FavoriteBookDatabase: RoomDatabase() {
    abstract val favoriteBookDao: FavoriteBookDao
    companion object {
        const val DB_NAME = "issam_books.db"
    }
}

