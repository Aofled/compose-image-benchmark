package ru.createsmart.composeimagebenchmark.feature.feed

import androidx.compose.runtime.Immutable
import ru.createsmart.composeimagebenchmark.core.model.ImageLoaderType
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio

@Immutable
internal sealed interface FeedUiState {

    @Immutable
    data object Loading : FeedUiState

    @Immutable
    data class Success(
        val items: List<BenchmarkItemUio>,
        val selectedLoaderType: ImageLoaderType = ImageLoaderType.DEFAULT,
    ) : FeedUiState

    @Immutable
    data class Error(
        val message: String? = null,
        val messageRes: Int? = null,
    ) : FeedUiState
}
