package ru.createsmart.composeimagebenchmark.feature.feed.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImage
import ru.createsmart.composeimagebenchmark.core.designsystem.component.BenchmarkCardLayout
import ru.createsmart.composeimagebenchmark.core.model.ImageLoaderType
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio
import ru.createsmart.composeimagebenchmark.feature.feed.util.toAvatarRequest

/**
 * Single feed item card.
 * Layout is same for all tests, only imageSlot changes.
 */
@Composable
internal fun FeedCard(
    item: BenchmarkItemUio,
    imageLoaderType: ImageLoaderType,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val avatarRequest = remember(item.id) { item.toAvatarRequest(context) }

    BenchmarkCardLayout(
        authorName = item.authorName,
        title = item.title,
        description = item.description,
        tags = item.tags,
        rating = item.rating,
        likesCount = item.likesCount,
        commentsCount = item.commentsCount,
        isBookmarked = item.isBookmarked,
        imageAspectRatio = item.aspectRatio,
        modifier = modifier,
        authorAvatarSlot = {
            AsyncImage(
                model = avatarRequest,
                imageLoader = imageLoader,
                contentDescription = item.authorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        },
        imageSlot = {
            FeedImageHost(
                item = item,
                imageLoaderType = imageLoaderType,
                imageLoader = imageLoader,
            )
        },
    )
}
