package org.example.project.book.data.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.example.project.book.data.dto.SearchResponseDto
import org.example.project.book.domain.Book
import org.example.project.core.data.safeCall
import org.example.project.core.domain.DataError
import org.example.project.core.domain.Error
import org.example.project.core.domain.Result


private const val BASE_URL = "https://openlibrary.org"
class KtorRemoteBookDataSource(
    private val httpClient: HttpClient
) {
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? =null
    ): Result<SearchResponseDto, DataError.Remote> { //As standard Result  take only one generic, we will write our own
       return safeCall{
           httpClient.get( //check out get(urlString, lambda as extension from RequestBuilder)
               urlString = "$BASE_URL/search.json"
           ){
               parameter("q", query) //paramter is extension from RequestBuilder: our lambda will be an extension using extensions
               parameter("limit", resultLimit)
               parameter("languages", "eng")
               parameter("fields", "key, title,author_name,author_key,cover_edition_key,cover_i,ratings_average,ratings_count,first_publish_year,language,number_of_pages_median,edition_count")
           }
           }
       }
}