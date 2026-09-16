package ru.createsmart.composeimagebenchmark.feature.feed.component.strategy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImage
import ru.createsmart.composeimagebenchmark.core.designsystem.component.shimmerEffect
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio
import ru.createsmart.composeimagebenchmark.feature.feed.util.toMainImageRequest

/**
 * Strategy 1: AsyncImage (Direct Canvas / No SubcomposeLayout).
 * Renders directly via Painter without extra LayoutNodes.
 */
@Composable
internal fun AsyncImageDirectSlot(
    item: BenchmarkItemUio,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val imageRequest = remember(item.id) { item.toMainImageRequest(context) }

    val errorColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
    var showShimmer by remember(item.id) { mutableStateOf(true) }

    AsyncImage(
        model = imageRequest,
        imageLoader = imageLoader,
        contentDescription = item.title,
        contentScale = ContentScale.Crop,
        error = remember(errorColor) { ColorPainter(errorColor) },
        onLoading = { showShimmer = true },
        onSuccess = { showShimmer = false },
        onError = { showShimmer = false },
        modifier = modifier
            .fillMaxSize()
            .shimmerEffect(enabled = showShimmer),
    )
}
