package org.example.project.book.presentation.book_list

import org.example.project.book.domain.Book
import org.example.project.core.presentation.UiText

data class BookListState(
    val searchQuery: String = "Issam",
    val searchResults: List<Book> = emptyList(),
    val favoriteBooks: List<Book> = emptyList(),
    val isLoading : Boolean = true,
    val selectedTabIndex: Int= 0,
    val errorMessage: UiText? = null
    )