package org.example.project.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.ktor.client.engine.HttpClientEngine
import org.example.project.book.data.network.KtorRemoteBookDataSource
import org.example.project.book.data.repository.DefaultBookRepository
import org.example.project.book.presentation.SelectedBookViewModel
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

            navigation<Route.BookGraph>(startDestination = Route.BookList) {

                composable<Route.BookList> {
                    val viewModel = koinViewModel<BookListViewModel>()
                    val selectedBokViewModel =
                        it.sharedKoinViewModel<SelectedBookViewModel>(navController) //it: navBackStack entry

                    LaunchedEffect(true) {
                        selectedBokViewModel.onSelectedBook(null)  //BookSelection at Launch is null
                    }

                    BookListScreenRoot(
                        viewModel = viewModel,
                        onBookClick = { book -> //when book clicked -> navigate
                            selectedBokViewModel.onSelectedBook(book) //integrate BookSelection VM at click

                            navController.navigate(
                                Route.BookDetail(book.id)
                            )
                        }
                    )
                }
                composable<Route.BookDetail> { entry -> //args we navigated with
                    val args = entry.toRoute<Route.BookDetail>()
                    val selectedBookViewModel =
                        entry.sharedKoinViewModel<SelectedBookViewModel>(navController) //my generic function will be Typed by SelectedBookVM
                    val selectedBook by selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Book is $selectedBook")
                    }
                }
            }
        }
    }
}


/* This function works with any typeof viewModel and any type of Nav Graph and screen feature structure.
  - restricting generic T to be a subClass of viewModel
  - reified so generic type cannot be removed at runtime - and so function and thus Koin can  access the actual type at RT(correct viewModel instance like T::class.java)
    => Without: call    -> sharedKoinViewModel(SelectedBookVM::class)
    => With : call      -> sharedKoinViewModel<SelectedBookVM>(navController)
*/

@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel( //Extension of NavBackStackEntry
    navController: NavController
): T {
    //1 - compute parentEntry(NavBackStackEntry) by navController.getBackStackEntry(Route: String)

    //if parent Graph' route is null then return KoinViewModel<T> without scoping this vm to parent navGraph
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this){ //remember "this" (of NavBackStackEntry) if it has the same value after recomposition, otherwise get it from calculation lambda{}
        navController.getBackStackEntry(navGraphRoute)
    }


    //2 - Explicitly scope this VM to parent Nav Graph(parentEntry),
    // rather than NavBackStackEntry that is scoped to a single screen
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )

}


