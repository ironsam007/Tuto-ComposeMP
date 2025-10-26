package org.example.project


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.book.domain.Book
import org.example.project.book.presentation.book_list.BookListScreen
import org.example.project.book.presentation.book_list.BookListState
import org.example.project.book.presentation.book_list.components.BookListItem
import org.example.project.book.presentation.book_list.components.BookSearchBar


//@Preview
@Composable
private fun BookSearchBarPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        BookSearchBar(
            searchQuery = "",
            onSearchQueryChange = {},
            onImeSearch = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

private val books = (1..100).map{
    Book(
        id = it.toString(),
        title = "Book $it",
        imageUrl = "https://test.com",
        authors = listOf("Issam Ab"),
        description = "Description $it",
        languages = emptyList(),
        firstPublishYear = null,
        averageRating = 4.67854,
        ratingCount = 5,
        numPages = 100,
        numEditions = 3
    )
}

@Preview
@Composable
fun BookListPreview(){
    BookListScreen(
        state = BookListState(
            searchResults = books
        ),
        onIntent = {}
    )
}