package org.example.project.book.presentation.book_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.book.domain.Book
import org.example.project.book.domain.BookRepository
import org.example.project.core.domain.onError
import org.example.project.core.domain.onSuccess





class BookListViewModel(
    private val bookRepository: BookRepository
): ViewModel() {



    private val _state = MutableStateFlow(BookListState()) //Internal stateFlow, can be modified on VM level
    val state = _state.asStateFlow() //exposed StateFlow that reflect the internal StateFlow

    private var searchJob: Job? = null // for concurrency control: cancel search if there is new one
    private var cachedBooks = emptyList<Book>()

    /*
    onIntent(): should be called within UI to handle all defined user intents on BookListScreen,
     */
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


    /*
     observeSearchQuery(): Listen to any state change in searchQuery and trigger research
    */
    private fun observerSearchQuery(){
        state
            .map {it.searchQuery} //create new Flow<String> that emits the latest searchQuery every time BookListState changes / Still a transformer but here, for over time
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                when{
                    query.isBlank() -> {
                        _state.update{
                            it.copy(
                                errorMessage = null,
                                searchResults = cachedBooks // added above
                            )
                        }
                    }

                    query.length >=2 -> {
                        searchJob?.cancel()             // cancel the old search Job
                        searchJob = searchBooks(query)  // launch new search job
                    }
                }
            }
            .launchIn(viewModelScope) //launch the collection of this flow in vm scope
    }

    private fun searchBooks(query: String) = viewModelScope.launch { //searchBooks should be asynchronous AND will be launched as viewModelScope coroutine
        _state.update {
            it.copy(
                isLoading = true
            )
        }

        bookRepository
            .searchBooks(query)
            .onSuccess { searchResults ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        searchResults = searchResults
                    )
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        searchResults = emptyList(),
                        isLoading = false,
                        errorMessage = error.toUiText() // to be created dataErrorToStringResources extension from dataError
                    )
                }
            }

    }
}