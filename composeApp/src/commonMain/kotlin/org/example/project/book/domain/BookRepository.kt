package org.example.project.book.domain

import org.example.project.core.domain.DataError
import org.example.project.core.domain.Result




interface BookRepository {
    suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote>

    suspend fun getBookDescription(bookId: String): Result<String?, DataError> //error can be remote and local

}