package ru.createsmart.composeimagebenchmark.feature.feed.mapper

import ru.createsmart.composeimagebenchmark.core.model.BenchmarkItem
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio

internal fun BenchmarkItem.toUio(): BenchmarkItemUio = BenchmarkItemUio(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    authorName = authorName,
    authorAvatarUrl = authorAvatarUrl,
    tags = tags,
    rating = rating,
    likesCount = likesCount,
    commentsCount = commentsCount,
    isBookmarked = isBookmarked,
    aspectRatio = aspectRatio,
)
