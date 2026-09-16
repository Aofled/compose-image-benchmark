package ru.createsmart.composeimagebenchmark.feature.feed.component.strategy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImage
import ru.createsmart.composeimagebenchmark.core.designsystem.component.ErrorPlaceholder
import ru.createsmart.composeimagebenchmark.core.designsystem.component.shimmerEffect
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio
import ru.createsmart.composeimagebenchmark.feature.feed.util.toMainImageRequest

/**
 * Strategy 2: AsyncImage + Composable Overlay (Hybrid approach).
 *
 * 1. Image renders fast via Canvas.
 * 2. Standard Box usage (no SubcomposeLayout).
 * 3. Composable error is shown if loading fails.
 */
@Composable
internal fun AsyncImageOverlaySlot(
    item: BenchmarkItemUio,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val imageRequest = remember(item.id) { item.toMainImageRequest(context) }

    var isLoading by remember(item.id) { mutableStateOf(true) }
    var isError by remember(item.id) { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model = imageRequest,
            imageLoader = imageLoader,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            onLoading = {
                isLoading = true
                isError = false
            },
            onSuccess = {
                isLoading = false
                isError = false
            },
            onError = {
                isLoading = false
                isError = true
            },
            modifier = Modifier
                .fillMaxSize()
                .shimmerEffect(enabled = isLoading),
        )

        if (isError) {
            ErrorPlaceholder(modifier = Modifier.fillMaxSize())
        }
    }
}
