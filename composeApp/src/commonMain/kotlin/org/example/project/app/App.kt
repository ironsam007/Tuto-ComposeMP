package org.example.project.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.ktor.client.engine.HttpClientEngine
import org.example.project.book.data.network.KtorRemoteBookDataSource
import org.example.project.book.data.repository.DefaultBookRepository
import org.example.project.book.presentation.book_list.BookListScreenRoot
import org.example.project.book.presentation.book_list.BookListViewModel
import org.example.project.core.data.HttpClientFactory
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(engine: HttpClientEngine) { //engine will be platform dependent, a value will be set for each pf
    BookListScreenRoot(
        viewModel = remember{ BookListViewModel(
            bookRepository = DefaultBookRepository(
                remoteBookDataSource = KtorRemoteBookDataSource(
                    httpClient = HttpClientFactory.create(engine)
                )
            )
        )},
        onBookClick = {}

    )
}