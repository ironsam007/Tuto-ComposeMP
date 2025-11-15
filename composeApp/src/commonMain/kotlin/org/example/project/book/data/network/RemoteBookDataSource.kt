package org.example.project.book.data.network

import org.example.project.book.data.dto.BookWorkDto
import org.example.project.book.data.dto.SearchResponseDto
import org.example.project.book.domain.BookRepository
import org.example.project.core.domain.DataError
import org.example.project.core.domain.Result


/*
=> "KtorRemoteBookDataSource" cannot be used app wide(in viewModel, domain layer) because is will violate clean Arch:
Presentation -> Domain <- Data. To work around this, u need to implement its function signature in domain(abstraction) and then  use app wide.

=> In our case, we have multiple data sources, so an abstraction is already needed on Repository level which can be used app wide.
=> As a consequence of this, we can keep another abstraction decalred in /data in order to use SearchBookDto as a return type.
*/

interface RemoteBookDataSource{
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? = null): Result<SearchResponseDto, DataError.Remote>

    suspend fun getBookDetails(bookWorkId: String): Result<BookWorkDto, DataError.Remote>

}

