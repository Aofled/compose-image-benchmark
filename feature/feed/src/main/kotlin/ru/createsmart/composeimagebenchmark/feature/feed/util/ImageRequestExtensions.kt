package ru.createsmart.composeimagebenchmark.feature.feed.util

import android.content.Context
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio

/**
 * Create image request with unified parameters.
 */
internal fun BenchmarkItemUio.toMainImageRequest(context: Context): ImageRequest {
    return ImageRequest.Builder(context)
        .data(imageUrl)
        .crossfade(enable = true)
        .build()
}

/**
 * Specific request for avatars.
 */
internal fun BenchmarkItemUio.toAvatarRequest(context: Context): ImageRequest {
    return ImageRequest.Builder(context)
        .data(authorAvatarUrl)
        .crossfade(enable = true)
        .scale(Scale.FILL)
        .build()
}
