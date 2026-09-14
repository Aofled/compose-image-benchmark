package ru.createsmart.composeimagebenchmark.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for asset JSON data.
 */
@Serializable
internal data class BenchmarkItemDto(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("image_path") val imagePath: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("author_avatar_path") val authorAvatarPath: String,
    @SerialName("tags") val tags: List<String>,
    @SerialName("rating") val rating: Float,
    @SerialName("likes_count") val likesCount: Int,
    @SerialName("comments_count") val commentsCount: Int,
    @SerialName("is_bookmarked") val isBookmarked: Boolean = false,
    @SerialName("aspect_ratio") val aspectRatio: Float,
    @SerialName("loading_delay_ms") val loadingDelayMs: Long,
    @SerialName("is_error") val isError: Boolean = false,
)
