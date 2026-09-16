package ru.createsmart.composeimagebenchmark.feature.feed.model

import androidx.compose.runtime.Immutable

@Immutable
internal data class BenchmarkItemUio(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val tags: List<String>,
    val rating: Float,
    val likesCount: Int,
    val commentsCount: Int,
    val isBookmarked: Boolean,
    val aspectRatio: Float,
)
