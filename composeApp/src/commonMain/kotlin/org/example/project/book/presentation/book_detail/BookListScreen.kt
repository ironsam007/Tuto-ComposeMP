package org.example.project.book.presentation.book_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.book.presentation.book_list.BookListState


@Composable
fun BookDetailScreenRoot(
    viewModel: BookDetailViewModel,
    onBookClick: () -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    BookDetailScreen(
        state = state,
        onAction = { action ->
            when(action){
                is BookDetailAction.OnBookClick -> onBookClick()
                else -> Unit
            }
            viewModel.onAction(action)

        }

    )
}

@Composable
fun BookDetailScreen(
    state: BookDetailState,
    onAction: (BookDetailAction)->Unit
) {

}