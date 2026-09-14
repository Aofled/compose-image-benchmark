package ru.createsmart.composeimagebenchmark.core.data.mapper

import ru.createsmart.composeimagebenchmark.core.data.model.BenchmarkItemDto
import ru.createsmart.composeimagebenchmark.core.model.BenchmarkItem

/**
 * Converts DTO to Domain model.
 */
internal fun BenchmarkItemDto.toDomain(): BenchmarkItem {
    // Add delay parameter to URL for BenchmarkDelayInterceptor.
    val fullImageUrl = if (isError) {
        "file:///android_asset/benchmark_images/non_existent_error.jpg?delay=$loadingDelayMs"
    } else {
        "file:///android_asset/$imagePath?delay=$loadingDelayMs"
    }

    val fullAvatarUrl = "file:///android_asset/$authorAvatarPath"

    return BenchmarkItem(
        id = id,
        title = title,
        description = description,
        imageUrl = fullImageUrl,
        authorName = authorName,
        authorAvatarUrl = fullAvatarUrl,
        tags = tags,
        rating = rating,
        likesCount = likesCount,
        commentsCount = commentsCount,
        isBookmarked = isBookmarked,
        aspectRatio = aspectRatio,
        loadingDelayMs = loadingDelayMs,
        isError = isError,
    )
}
