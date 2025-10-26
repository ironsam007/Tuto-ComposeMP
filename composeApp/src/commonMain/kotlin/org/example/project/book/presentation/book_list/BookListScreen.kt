package org.example.project.book.presentation.book_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.book.domain.Book
import org.example.project.book.presentation.book_list.components.BookSearchBar
import org.example.project.core.presentation.DarkBlue


@Composable
fun BookListScreenRoot(
    viewModel: BookListViewModel,
    onBookClick: (Book) -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle() //collect only when UI is active preventing leaks and wasted works.(recommanded to be used by defaults from google)

    //Calling BookListScreen with state, and onIntent() callback
    //lambda passed for onIntent() trigger onBookClick if the intent is BookClick, and vm.onIntent() otherwise
    BookListScreen(
        state,
        onIntent = { intent ->
            when(intent)
            {
                is BookListIntent.OnBookClick -> onBookClick(intent.book)
                else -> Unit
            }
            viewModel.onIntent(intent)
        }
    )
}


// Screen will be fed by VM with MVI structure:
@Composable
fun BookListScreen(
    state: BookListState,
    onIntent: (BookListIntent) -> Unit
){
    val keyboardController = LocalSoftwareKeyboardController.current //gets a reference to the on-screen keyboard controller — the system object that can manually show or hide the soft keyboard (IME)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        BookSearchBar(
            searchQuery =state.searchQuery,
            onSearchQueryChange = {
                onIntent(BookListIntent.OnSearchQueryChange(it))
            },
            onImeSearch ={
                keyboardController?.hide()
            },
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(16.dp)
        )
    }


}
