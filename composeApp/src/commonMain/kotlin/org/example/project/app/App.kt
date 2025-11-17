package org.example.project.app


import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import org.example.project.book.presentation.SelectedBookViewModel
import org.example.project.book.presentation.book_detail.BookDetailAction
import org.example.project.book.presentation.book_detail.BookDetailScreenRoot
import org.example.project.book.presentation.book_detail.BookDetailViewModel
import org.example.project.book.presentation.book_list.BookListScreenRoot
import org.example.project.book.presentation.book_list.BookListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel



@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.BookGraph
        ) {   //Define Navigation graph

            navigation<Route.BookGraph>(startDestination = Route.BookList)
            {
                composable<Route.BookList> {
                    //this vm only lives while BookListScreen is on BackStack
                    val viewModel = koinViewModel<BookListViewModel>()

                    //This one can outlive any BackStackEntry/Screen, will be scoped to parent Nav Graph(parentEntry) of the NavBackStackEntry
                    val selectedBokViewModel = it.sharedKoinViewModel<SelectedBookViewModel>(navController)

                    //Unselect Book when you are on BookList
                    LaunchedEffect(true) {
                        selectedBokViewModel.onSelectedBook(null)
                    }

                    //Render BookListScreen
                    BookListScreenRoot(
                        viewModel = viewModel,
                        onBookClick = { book -> //when book clicked -> navigate
                            selectedBokViewModel.onSelectedBook(book) //Update selected Book in BookSelection VM
                            navController.navigate(
                                Route.BookDetail(book.id)   //Navigate to BookDetail of passed book.id
                            )
                        }
                    )
                }

                //Adding Animation for BookDetailScreen transition(enter, exit)
                composable<Route.BookDetail>(
                    enterTransition = { slideInHorizontally { initialOffset -> initialOffset } },
                    exitTransition = { slideOutHorizontally { initialOffset -> initialOffset } }
                ) {
                    //Scoped to Parent NavGraph
                    val selectedBookViewModel = it.sharedKoinViewModel<SelectedBookViewModel>(navController)

                    //get the selected Book in BookList from Shared viewModel
                    val selectedBook by selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()

                    //get an instance of BookDetailViewModel from koin
                    val viewModel = koinViewModel<BookDetailViewModel>()

                    //Render BookDetail screen
                    BookDetailScreenRoot(
                        viewModel = viewModel,
                        onBackClick = {
                            navController.navigateUp() //navigate to previous screen when clicked on Back
                        }
                    )

                    //when selectedBook change submit that change to BookDetail VM so that BookDetailScreen can be recomposed
                    LaunchedEffect(selectedBook){
                        selectedBook?.let{
                            viewModel.onAction(BookDetailAction.OnSelectedBookChange(it))
                        }
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


