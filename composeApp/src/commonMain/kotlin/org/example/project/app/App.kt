package org.example.project.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.ktor.client.engine.HttpClientEngine
import org.example.project.book.data.network.KtorRemoteBookDataSource
import org.example.project.book.data.repository.DefaultBookRepository
import org.example.project.book.presentation.book_list.BookListScreenRoot
import org.example.project.book.presentation.book_list.BookListViewModel
import org.example.project.core.data.HttpClientFactory
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
//    val viewModel = koinViewModel<BookListViewModel>()
//    BookListScreenRoot(
//        viewModel = viewModel,
//        onBookClick = {}
//    )

    MaterialTheme{
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.BookGraph
        ){

        }

    }

}