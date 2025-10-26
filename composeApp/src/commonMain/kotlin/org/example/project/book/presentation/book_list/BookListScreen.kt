package org.example.project.book.presentation.book_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.book.domain.Book
import org.example.project.book.presentation.book_list.components.BookList
import org.example.project.book.presentation.book_list.components.BookSearchBar
import org.example.project.core.presentation.DarkBlue
import org.example.project.core.presentation.DesertWhite
import org.example.project.core.presentation.SandYellow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tuto_composemp.composeapp.generated.resources.Res
import tuto_composemp.composeapp.generated.resources.favorites
import tuto_composemp.composeapp.generated.resources.no_favorite_books
import tuto_composemp.composeapp.generated.resources.no_search_results
import tuto_composemp.composeapp.generated.resources.search_results


@Composable
fun BookListScreenRoot(
    viewModel: BookListViewModel = koinViewModel(),
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

    val pagerState = rememberPagerState { 2 }
    val searchResultsListState = rememberLazyListState()
    val favoriteBooksListState = rememberLazyListState()


    LaunchedEffect(state.searchResults) { //added lately to force animation when results change
        searchResultsListState.animateScrollToItem(0)
    }

    // when click on header, it shall switch the page: done by LaunchedEffect:
    // Page By Tab
    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    //Tab by Page
    LaunchedEffect(pagerState.currentPage) {
        onIntent(BookListIntent.OnTabSelected(pagerState.currentPage))
    }


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

        // version 2 - add Surface and TabRow for BookList: UI control bar with labeled tabs
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = DesertWhite,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            )
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .widthIn(max = 700.dp)
                        .fillMaxWidth(),
                    containerColor = DesertWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator( //Todo: overwrite this indicator to offset the tab - remove purple bar
                            color = SandYellow,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                        )
                    }
                ){
                    Tab(
                        selected = state.selectedTabIndex ==0,
                        onClick = {onIntent(BookListIntent.OnTabSelected(0))},
                        modifier= Modifier.weight(1f),
                        selectedContentColor = SandYellow,
                        unselectedContentColor = Color.Black.copy(alpha = 0.5f)
                    ){
                        Text(
                            text = stringResource(Res.string.search_results),
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )
                    }
                    Tab(
                        selected = state.selectedTabIndex ==1,
                        onClick = {onIntent(BookListIntent.OnTabSelected(1))},
                        modifier= Modifier.weight(1f),
                        selectedContentColor = SandYellow,
                        unselectedContentColor = Color.Black.copy(alpha = 0.5f)
                    ){
                        Text(
                            text = stringResource(Res.string.favorites),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                //version3 - add HP to swip between Favorite and searchResults pages- it provides pager states(tack page, scroll progress)
                HorizontalPager(
                    state = pagerState, // declared in function scop
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ){pageIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        when(pageIndex){
                            0 -> {
                                if(state.isLoading){
                                    CircularProgressIndicator()
                                }else{
                                    when{
                                        state.errorMessage !=null -> {
                                            Text(
                                                text = state.errorMessage.asString(), //unwrap UiText object by asString()
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        state.searchResults.isEmpty() -> {
                                            Text(
                                                text =  stringResource(Res.string.no_search_results),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        else -> {
                                            BookList(
                                                books = state.searchResults,
                                                onBookClick = {
                                                    onIntent(BookListIntent.OnBookClick(it))
                                                },
                                                modifier = Modifier.fillMaxSize(),
                                                scrollState = searchResultsListState //declared in function scop: rememberLazyListState
                                            )
                                        }
                                    }
                                }
                            }
                            1 -> {
                                if(state.favoriteBooks.isEmpty()){
                                    Text(
                                        text = stringResource(Res.string.no_favorite_books),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineSmall,
                                    )
                                }else{
                                    BookList(
                                        books = state.favoriteBooks,
                                        onBookClick = {onIntent(BookListIntent.OnBookClick(it))},
                                        modifier  = Modifier.fillMaxSize(),
                                        scrollState = favoriteBooksListState

                                    )
                                }

                            }
                        }
                    }

                }


            }
        }

    }


}
