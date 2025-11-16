package org.example.project.book.data.database

import androidx.room.RoomDatabase

//create a builder for Room Database, typed with our DB implementation and actual by each platform
expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<FavoriteBookDatabase>
}