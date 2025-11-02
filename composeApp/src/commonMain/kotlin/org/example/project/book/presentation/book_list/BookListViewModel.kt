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
): ViewModel() {

    private var searchJob: Job? = null // for concurrency control: cancel search if there is new one
    private var cachedBooks = emptyList<Book>()



    private val _state = MutableStateFlow(BookListState()) //Internal stateFlow, can be modified on VM level

    /*
    With .asStateFlow() only expose as readOnly stateflow:
        -> no lifeCycle awareness: if no one collects, _state still exists but u can't delay or controll emission:
        -> can't intercept when collection start: ability to run logic when ui starts observing.
    With .onStart{} u can run side effects when the flow starts being collected,
        -> in our case, call observeSearchQuery ony when cache is empty
            => Lazily initialize only when needed, not immediately or at construction.

    With .stateIn(): convert Flow into Hot StateFlow with controller sharing rules.

    */
    val state = _state
        .onStart { //when flow start being collected
        if (cachedBooks.isEmpty()) { //even after the flow start being collected, we will lazily observe search query only when cachedBooks is empty
            observeSearchQuery()
        }
        }
        .stateIn( //converting this flow into hot stateFlow, with sharing rules:
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L), //After the last collector stops wait 5s before stopping upstream(this prevent rapid start/stop when screen rotate)
            initialValue = _state.value // initial value to start from
            )



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
    private fun observeSearchQuery(){
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