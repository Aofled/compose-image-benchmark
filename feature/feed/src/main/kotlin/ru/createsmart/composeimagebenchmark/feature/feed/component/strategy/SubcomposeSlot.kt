package ru.createsmart.composeimagebenchmark.feature.feed.component.strategy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import ru.createsmart.composeimagebenchmark.core.designsystem.component.ErrorPlaceholder
import ru.createsmart.composeimagebenchmark.core.designsystem.component.shimmerEffect
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio
import ru.createsmart.composeimagebenchmark.feature.feed.util.toMainImageRequest

/**
 * Strategy 3: SubcomposeAsyncImage with slot architecture.
 *
 * Uses SubcomposeLayout for content branches (loading, error, success).
 * High flexibility, but slower Measure and Layout phases.
 */
@Composable
internal fun SubcomposeSlot(
    item: BenchmarkItemUio,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val imageRequest = remember(item.id) { item.toMainImageRequest(context) }

    SubcomposeAsyncImage(
        model = imageRequest,
        imageLoader = imageLoader,
        contentDescription = item.title,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize(),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect(),
            )
        },
        error = {
            ErrorPlaceholder(
                modifier = Modifier.fillMaxSize(),
            )
        },
    )
}
