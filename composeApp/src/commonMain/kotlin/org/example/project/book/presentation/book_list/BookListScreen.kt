package org.example.project.book.presentation.book_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.book.domain.Book


@Composable
fun BookListScreenRoot(
    viewModel: BookListViewModel,
    onBookClick: (Book) -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle() //collect only when UI is active preventing leaks and wasted works.(recommanded to be used by defaults from google)
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
}
