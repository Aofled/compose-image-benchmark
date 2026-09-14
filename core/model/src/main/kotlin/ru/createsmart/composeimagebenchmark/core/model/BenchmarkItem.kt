package ru.createsmart.composeimagebenchmark.core.model

/**
 * Data model for stress testing Compose phases: Composition, Measure, and Draw.
 *
 * @property id Unique ID.
 * @property title Card title.
 * @property description Long text to test TextLayoutCache.
 * @property imageUrl Main image path.
 * @property authorName Author name.
 * @property authorAvatarUrl Avatar path.
 * @property tags Used to test FlowRow (Measure/Layout load).
 * @property rating Used for custom Canvas drawing tests.
 * @property likesCount Likes count.
 * @property commentsCount Comments count.
 * @property isBookmarked Dynamic icon state.
 * @property aspectRatio Image aspect ratio (affects Measure/Layout phase).
 *      AspectRatio in benchmark_feed.json is calculated from the aspect ratio of the image
 * @property loadingDelayMs Network or decoding delay simulation (ms).
 *      loadingDelayMs in benchmark_feed.json: loading_delay_ms = 30+(sizeInKb×2.0),
 *      where 30 is the base average network ping (~25-35 ms)
 * @property isError Force error state for UI tests.
 */
public data class BenchmarkItem(
    public val id: Long,
    public val title: String,
    public val description: String,
    public val imageUrl: String,
    public val authorName: String,
    public val authorAvatarUrl: String,
    public val tags: List<String>,
    public val rating: Float,
    public val likesCount: Int,
    public val commentsCount: Int,
    public val isBookmarked: Boolean = false,
    public val aspectRatio: Float = DEFAULT_ASPECT_RATIO,
    public val loadingDelayMs: Long = DEFAULT_LOADING_DELAY,
    public val isError: Boolean = false,
) {
    public companion object {
        public const val DEFAULT_ASPECT_RATIO: Float = 16f / 9f
        public const val DEFAULT_LOADING_DELAY: Long = 150L
    }
}
