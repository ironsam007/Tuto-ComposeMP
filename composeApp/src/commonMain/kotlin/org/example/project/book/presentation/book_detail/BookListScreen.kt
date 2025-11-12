package org.example.project.book.presentation.book_detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.book.presentation.book_detail.components.BlurredImageBackground
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
    BlurredImageBackground(
        imageUrl = state.book?.imageUrl,
        isFavorite = state.isFavorite,
        onFavoriteClick = {
            onAction(BookDetailAction.OnFavoriteClick)
        },
        onBackClick = {
            onAction(BookDetailAction.OnBookClick)
        },
        modifier = Modifier.fillMaxSize()
    ){}

}