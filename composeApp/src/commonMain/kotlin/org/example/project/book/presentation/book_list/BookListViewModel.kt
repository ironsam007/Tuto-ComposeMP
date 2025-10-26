package org.example.project.book.presentation.book_list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class BookListViewModel(

): ViewModel() {
    private val _state = MutableStateFlow(BookListState())
    val state = _state.asStateFlow()

    fun onIntent(intent: BookListIntent){
        when(intent){
            is BookListIntent.OnBookClick -> {

            }
            is BookListIntent.OnTabSelected -> {
                _state.update{
                    it.copy(selectedTabIndex = intent.index)
                }
            }
            is BookListIntent.OnSearchQueryChange -> {
                //_state.value.copy(searchQuery = intent.query) not thread safe, risk for concurrent update
                _state.update{
                    it.copy(intent.query)
                }
            }
        }
    }
}