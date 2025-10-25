package org.example.project.book.presentation.book_list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class BookListViewModel(

): ViewModel() {
    private val _state = MutableStateFlow(BookListState())
    val state = _state.asStateFlow()

    fun onIntent(action: BookListIntent){
        when(action){
            is BookListIntent.OnBookClick -> {

            }
            is BookListIntent.OnTabSelected -> {

            }
            is BookListIntent.OnSearchQueryChange -> {

            }
        }
    }
}