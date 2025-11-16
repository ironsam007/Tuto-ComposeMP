package org.example.project.book.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.example.project.book.domain.Book


@Dao
interface FavoriteBookDao{

    @Upsert
    suspend fun upsert(book: BookEntity) // insert a book in room db

    @Query("SELECT * FROM BookEntity")
    suspend fun getFavoriteBooks(): Flow<List<BookEntity>> //get all books

    @Query("SELECT * FROM BookEntity WHERE id = :id")
    suspend fun getFavoriteBook(id : String): BookEntity? //get book by id

    @Query("DELETE FROM BookEntity WHERE id = :id")
    suspend fun deleteFavoriteBook(id: String)
    
}