package org.example.project.book.data.network

import io.ktor.client.HttpClient
import org.example.project.book.domain.Book

class KtorRemoteBookDataSource(
    private val httpClient: HttpClient
) {
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? =null
    ): Result<List<Book>, DataError.Remote> {

    }
}