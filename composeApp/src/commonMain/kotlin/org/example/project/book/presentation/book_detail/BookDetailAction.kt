package org.example.project.book.presentation.book_detail

import org.example.project.book.domain.Book


sealed interface BookDetailAction {
    data object OnBookClick: BookDetailAction
    data object OnFavoriteClick: BookDetailAction
    data class OnSelectedBookChange(val book: Book): BookDetailAction
}