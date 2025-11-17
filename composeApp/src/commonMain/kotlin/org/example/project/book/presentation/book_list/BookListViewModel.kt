package org.example.project.book.presentation.book_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.book.domain.Book
import org.example.project.book.domain.BookRepository
import org.example.project.core.domain.onError
import org.example.project.core.domain.onSuccess
import org.example.project.core.presentation.toUiText


class BookListViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {

    private var cachedBooks = emptyList<Book>()
    private var searchJob: Job? = null // for concurrency control: cancel search if there is new one
    private var observeFavoriteJob: Job? = null //this needed cuz u call the function in onSTart and the job could les 5s

    private val _state = MutableStateFlow(BookListState())

    /*
    With .asStateFlow() only expose as readOnly stateflow:
        -> no lifeCycle awareness: if no one collects, _state still exists but u can't delay or controll emission:
        -> can't intercept when collection start: ability to run logic when ui starts observing.
    With .onStart{} u can run side effects when the flow starts being collected,
        -> in our case, call observeSearchQuery ony when cache is empty
            => Lazily initialize only when needed, not immediately or at construction.

    With .stateIn(): convert Flow into Hot StateFlow with controller sharing rules.

    */
    val state = _state.onStart {
        if(cachedBooks.isEmpty()) {
            observeSearchQuery()
        }
        observeFavoriteBooks()
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun onIntent(action: BookListIntent) {
        when (action) {
            is BookListIntent.OnBookClick -> {

            }

            is BookListIntent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(searchQuery = action.query)
                }
            }

            is BookListIntent.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }
        }
    }

    //++
    private fun observeFavoriteBooks() {
        observeFavoriteJob?.cancel()
        observeFavoriteJob = bookRepository
            .getFavoriteBooks()
            .onEach { favoriteBooks ->
                _state.update { it.copy(
                    favoriteBooks = favoriteBooks
                ) }
            }
            .launchIn(viewModelScope)
    }

    //listen to state change in searchQuery and trigger research
    private fun observeSearchQuery() {
        state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L) //stop typing inter-temp
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _state.update {
                            it.copy(
                                errorMessage = null,
                                searchResults = cachedBooks
                            )
                        }
                    }

                    query.length >= 2 -> {
                        searchJob?.cancel() //SSS from searchBooks
                        searchJob = searchBooks(query)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchBooks(query: String) = viewModelScope.launch { //lately make body inside viewModelScope, more concurrency control, cancel previous search if there is new one (easy with job) check SSS
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
                        errorMessage = error.toUiText() //create DataErrorToStringResource extention from DataError
                    )
                }
            }
    }
}