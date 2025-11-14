package org.example.project.book.presentation.book_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.book.presentation.book_detail.components.BlurredImageBackground
import org.example.project.book.presentation.book_detail.components.BookChip
import org.example.project.book.presentation.book_detail.components.ChipSize
import org.example.project.book.presentation.book_detail.components.TitledContent
import org.example.project.core.presentation.SandYellow
import org.jetbrains.compose.resources.stringResource
import tuto_composemp.composeapp.generated.resources.Res
import tuto_composemp.composeapp.generated.resources.description_unavailable
import tuto_composemp.composeapp.generated.resources.languages
import tuto_composemp.composeapp.generated.resources.pages
import tuto_composemp.composeapp.generated.resources.rating
import tuto_composemp.composeapp.generated.resources.synopsis
import kotlin.math.round


@Composable
fun BookDetailScreenRoot(
    viewModel: BookDetailViewModel,
    onBackClick: () -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    BookDetailScreen(
        state = state,
        onAction = { action ->
            when(action){
                is BookDetailAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)

        }

    )
}

@Composable
private fun BookDetailScreen(
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit
) {
    // Main background Wrapper: shows blurred image, favorite button, back button
    // AND provide a slot for screen content()
    BlurredImageBackground(
        imageUrl = state.book?.imageUrl,
        isFavorite = state.isFavorite,
        onFavoriteClick = {
            onAction(BookDetailAction.OnFavoriteClick) //Notify viewModel of favorite toggle
        },
        onBackClick = {
            onAction(BookDetailAction.OnBackClick) //Notify viewModel of back navigation
        },
        modifier = Modifier.fillMaxSize()
    ) {

        // Only show book details if the book data is available
        if(state.book != null) {

            // Scrollable column containing all book information
            Column(
                modifier = Modifier
                    .widthIn(max = 700.dp) // Limit max width on large screens(tablets)
                    .fillMaxWidth()
                    .padding(
                        vertical = 16.dp,
                        horizontal = 24.dp
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Book title
                Text(
                    text = state.book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // authors list
                Text(
                    text = state.book.authors.joinToString(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                // Row for rating and page count chips
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    //Rating chip (if available)
                    state.book.averageRating?.let { rating ->
                        TitledContent(
                            title = stringResource(Res.string.rating),
                        ) {
                            BookChip {
                                Text(
                                    text = "${round(rating * 10) / 10.0}"
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = SandYellow
                                )
                            }
                        }
                    }

                    //page count chip (if available)
                    state.book.numPages?.let { pageCount ->
                        TitledContent(
                            title = stringResource(Res.string.pages),
                        ) {
                            BookChip {
                                Text(text = pageCount.toString())
                            }
                        }
                    }
                }

                // languages section (shown only if there is at leats one language)
                if(state.book.languages.isNotEmpty()) { //show all the langages
                    TitledContent(
                        title = stringResource(Res.string.languages),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        ) {

                            //Render each language as a small chip
                            state.book.languages.forEach { language ->
                                BookChip(
                                    size = ChipSize.SMALL,
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        text = language.uppercase(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                //"Synopsis" section title
                Text(
                    text = stringResource(Res.string.synopsis),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                            bottom = 8.dp
                        )
                )

                // If the description is still loading, show a loader
                if(state.isLoading) {
                    CircularProgressIndicator()
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f),
//                        contentAlignment = Alignment.Center
//                    ) {
//                    }
                } else {
                    // Display book description or fallback if missing
                    Text(
                        text = if(state.book.description.isNullOrBlank()) {
                            stringResource(Res.string.description_unavailable)
                        } else {
                            state.book.description
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        color = if(state.book.description.isNullOrBlank()) {
                            Color.Black.copy(alpha = 0.4f)//faded text for unavailable description
                        } else Color.Black,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}