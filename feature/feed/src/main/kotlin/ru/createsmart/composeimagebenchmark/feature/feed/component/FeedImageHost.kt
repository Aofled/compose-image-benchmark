package ru.createsmart.composeimagebenchmark.feature.feed.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import ru.createsmart.composeimagebenchmark.core.model.ImageLoaderType
import ru.createsmart.composeimagebenchmark.feature.feed.component.strategy.AsyncImageDirectSlot
import ru.createsmart.composeimagebenchmark.feature.feed.component.strategy.AsyncImageOverlaySlot
import ru.createsmart.composeimagebenchmark.feature.feed.component.strategy.PainterBoxSlot
import ru.createsmart.composeimagebenchmark.feature.feed.component.strategy.SubcomposeContentSlot
import ru.createsmart.composeimagebenchmark.feature.feed.component.strategy.SubcomposeSlot
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio

/**
 * Strategy selector for image rendering.
 * Switches implementation based on ImageLoaderType.
 */
@Composable
internal fun FeedImageHost(
    item: BenchmarkItemUio,
    imageLoaderType: ImageLoaderType,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    when (imageLoaderType) {
        ImageLoaderType.ASYNC_IMAGE_DIRECT -> {
            AsyncImageDirectSlot(
                item = item,
                imageLoader = imageLoader,
                modifier = modifier,
            )
        }
        ImageLoaderType.ASYNC_IMAGE_OVERLAY -> {
            AsyncImageOverlaySlot(
                item = item,
                imageLoader = imageLoader,
                modifier = modifier,
            )
        }
        ImageLoaderType.SUBCOMPOSE -> {
            SubcomposeSlot(
                item = item,
                imageLoader = imageLoader,
                modifier = modifier,
            )
        }
        ImageLoaderType.SUBCOMPOSE_CONTENT_SLOT -> {
            SubcomposeContentSlot(
                item = item,
                imageLoader = imageLoader,
                modifier = modifier,
            )
        }
        ImageLoaderType.PAINTER_BOX -> {
            PainterBoxSlot(
                item = item,
                imageLoader = imageLoader,
                modifier = modifier,
            )
        }
    }
}
