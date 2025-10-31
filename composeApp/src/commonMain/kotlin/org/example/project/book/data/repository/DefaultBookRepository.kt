package org.example.project.book.data.repository

import org.example.project.book.data.mappers.toBook
import org.example.project.book.data.network.RemoteBookDataSource
import org.example.project.book.domain.Book
import org.example.project.book.domain.BookRepository
import org.example.project.core.domain.DataError
import org.example.project.core.domain.Result
import org.example.project.core.domain.map




class DefaultBookRepository(
    private val remoteBookDataSource: RemoteBookDataSource
): BookRepository{
    override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote >{
        return remoteBookDataSource.searchBooks(query).map { dto -> dto.results.map { it -> it.toBook() } }
    }
}

