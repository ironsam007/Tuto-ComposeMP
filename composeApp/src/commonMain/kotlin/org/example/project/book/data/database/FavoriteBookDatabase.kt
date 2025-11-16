package org.example.project.book.data.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [BookEntity::class], //our database contains this table with header = BookEntity
    version =  1
)
abstract class FavoriteBookDatabase: RoomDatabase() {
    abstract val favoriteBookDao: FavoriteBookDao
    companion object {
        const val DB_NAME = "issam_books.db"
    }
}

