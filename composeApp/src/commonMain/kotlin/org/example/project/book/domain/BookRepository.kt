package org.example.project.book.domain

import kotlinx.coroutines.flow.Flow
import org.example.project.core.domain.DataError
import org.example.project.core.domain.EmptyResult
import org.example.project.core.domain.Result




interface BookRepository {
    suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote>
    suspend fun getBookDescription(bookId: String): Result<String?, DataError> //error can be remote and local

    fun getFavoriteBooks(): Flow<List<Book>> //List<BookEntity> need mapper so dont use data/BookEntity
    fun isBookFavorite(id: String): Flow<Boolean>
    suspend fun markAsFavorite(book: Book): EmptyResult<DataError.Local> //typealias for Result<Unit, E>
    suspend fun deleteFromFavorites(id: String)

}