package org.example.project.book.domain

import org.example.project.core.domain.DataError
import org.example.project.core.domain.Result




interface BookRepository {
    suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote>
}