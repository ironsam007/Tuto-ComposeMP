package org.example.project.book.data.network

import io.ktor.client.HttpClient
import org.example.project.book.domain.Book
import org.example.project.core.domain.DataError

class KtorRemoteBookDataSource(
    private val httpClient: HttpClient
) {
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? =null
    ): Result<List<Book>> { //As standard Result  take only one generic, we will write our own

    }
}