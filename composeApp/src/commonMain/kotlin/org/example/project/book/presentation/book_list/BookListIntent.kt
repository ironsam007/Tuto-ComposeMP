package org.example.project.book.presentation.book_list

import org.example.project.book.domain.Book


//we could use a sealed class but lets consider:
//interface:
//   - Flexible: new implementations can be added freely
//   - Not type safe: compiler doesn't know all implementations (When Exh check: no, must use if)

//sealed class:
//    - Less flexible: can't be extended from outside the file
//    - Type safe, ideal for fixed sets

//sealed interface:
//    - Flexible, still can't be extended from outside the file but it solves Kotlin Diamond problem for multiple class inheritance
//    - Type safe, closed set of implementations (When Exh check: yes)

sealed interface BookListIntent{
    data class OnSearchQueryChange(val query: String): BookListIntent
    data class OnTabSelected(val index: Int ): BookListIntent
    data class OnBookClick(val book: Book): BookListIntent
}

