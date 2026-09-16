package ru.createsmart.composeimagebenchmark.feature.feed.component.strategy

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import ru.createsmart.composeimagebenchmark.core.designsystem.component.ErrorPlaceholder
import ru.createsmart.composeimagebenchmark.core.designsystem.component.shimmerEffect
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio
import ru.createsmart.composeimagebenchmark.feature.feed.util.toMainImageRequest

/**
 * Strategy 5: rememberAsyncImagePainter with manual Box state.
 *
 * No SubcomposeLayout. Uses standard recomposition for state changes.
 */
@Composable
internal fun PainterBoxSlot(
    item: BenchmarkItemUio,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val imageRequest = remember(item.id) { item.toMainImageRequest(context) }

    val painter = rememberAsyncImagePainter(
        model = imageRequest,
        imageLoader = imageLoader,
        contentScale = ContentScale.Crop,
    )
    val state by painter.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (state) {
            is AsyncImagePainter.State.Loading,
            is AsyncImagePainter.State.Empty,
            -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect(),
                )
            }

            is AsyncImagePainter.State.Error -> {
                ErrorPlaceholder(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
