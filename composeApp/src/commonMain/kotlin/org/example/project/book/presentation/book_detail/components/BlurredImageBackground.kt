package org.example.project.book.presentation.book_detail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import org.example.project.core.presentation.DarkBlue
import org.example.project.core.presentation.DesertWhite
import org.example.project.core.presentation.PulseAnimation
import org.example.project.core.presentation.SandYellow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tuto_composemp.composeapp.generated.resources.Res
import tuto_composemp.composeapp.generated.resources.book_cover
import tuto_composemp.composeapp.generated.resources.book_error_1
import tuto_composemp.composeapp.generated.resources.go_back
import tuto_composemp.composeapp.generated.resources.mark_as_favorite
import tuto_composemp.composeapp.generated.resources.remove_from_favorites

@Composable
fun BlurredImageBackground( //[version final]
    imageUrl: String?,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    // Holds the result of the image loading (success, failure, null while loading)
    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }

    //Loads the image asynchronously and validate its size when successful
    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        onSuccess = {
            val size = it.painter.intrinsicSize
            imageLoadResult = if(size.width > 1 && size.height > 1) {
                Result.success(it.painter)
            } else {
                Result.failure(Exception("Invalid image dimensions"))
            }
        },
        onError = {
            it.result.throwable.printStackTrace()
        }
    )

    //Main container for the entire screen
    Box(modifier = modifier) {

        //Top blurred image + bottom background color layout
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            //To section: blurred background image area (30% height)
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .background(DarkBlue)
            ) {
                Image(
                    painter = painter,
                    contentDescription = stringResource(Res.string.book_cover),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp) //apply blur effect on background image
                )
            }

            //Bottom section: simple background fill for the lower part (70% height)
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth()
                    .background(DesertWhite) //Try this out now
            )
        }

        //Back arrow button positioned on top-left of the screen
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.go_back),
                tint = Color.White
            )
        }

        //Column containing the card(book image) and below it the passed screen content()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            //Spacing to push the card downward
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))

            //card that displays either the loaded book image or a placeholder/error image
            ElevatedCard(
                modifier = Modifier
                    .height(230.dp)
                    .aspectRatio(2 / 3f),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 15.dp
                )
            ) {

                // Transition between loading, success and error states
                AnimatedContent(targetState = imageLoadResult) { result ->
                    when(result) {

                        //still loading: Slow pulse Animation
                        null -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            PulseAnimation(
                                modifier = Modifier
                                    .size(60.dp)
                            )
                        }

                        //finished loading(success or error)
                        else -> {
                            Box {

                                //Display loaded image or fallback image based on result
                                Image(
                                    painter = if(result.isSuccess) painter else {
                                        painterResource(Res.drawable.book_error_1)
                                    },
                                    contentDescription = stringResource(Res.string.book_cover),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent),
                                    contentScale = if(result.isSuccess) {
                                        ContentScale.Crop
                                    } else {
                                        ContentScale.Fit
                                    }
                                )

                                //Favorite/unfavorite floating heart button
                                IconButton(
                                    onClick = onFavoriteClick,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    SandYellow, Color.Transparent
                                                ),
                                                radius = 70f
                                            )
                                        )
                                ) {
                                    Icon(
                                        imageVector = if(isFavorite) {
                                            Icons.Filled.Favorite
                                        } else {
                                            Icons.Outlined.FavoriteBorder
                                        },
                                        tint = Color.Red,
                                        contentDescription = if(isFavorite) {
                                            stringResource(Res.string.remove_from_favorites)
                                        } else {
                                            stringResource(Res.string.mark_as_favorite)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            //extra composable content to be passed by caller
            content()
        }
    }
}