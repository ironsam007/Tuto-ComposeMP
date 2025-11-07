package org.example.project.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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


    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.BookGraph
        ) {   //Define Navigation graph
            composable<Route.BookList> {
                val viewModel = koinViewModel<BookListViewModel>()
                BookListScreenRoot(
                    viewModel = viewModel,
                    onBookClick = { book -> //when book clicked -> navigate
                        navController.navigate(
                            Route.BookDetail(book.id)
                        )
                    }
                )
            }
            composable<Route.BookDetail> { entry -> //args we navigated with
                val args = entry.toRoute<Route.BookDetail>()
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Book is ${args.id}")
                }
            }
        }
    }
}