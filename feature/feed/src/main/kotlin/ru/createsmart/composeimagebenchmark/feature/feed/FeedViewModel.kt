package ru.createsmart.composeimagebenchmark.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.createsmart.composeimagebenchmark.core.domain.usecase.GetBenchmarkFeedUseCase
import ru.createsmart.composeimagebenchmark.core.model.BenchmarkItem
import ru.createsmart.composeimagebenchmark.core.model.ImageLoaderType
import ru.createsmart.composeimagebenchmark.feature.feed.mapper.toUio
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    getBenchmarkFeedUseCase: GetBenchmarkFeedUseCase,
    val imageLoader: ImageLoader,
) : ViewModel() {

    private val _selectedLoaderType = MutableStateFlow(ImageLoaderType.DEFAULT)

    val uiState: StateFlow<FeedUiState> = combine<List<BenchmarkItem>, ImageLoaderType, FeedUiState>(
        getBenchmarkFeedUseCase(),
        _selectedLoaderType,
    ) { items, loaderType ->
        FeedUiState.Success(
            items = items.map { it.toUio() },
            selectedLoaderType = loaderType,
        )
    }
        .catch { throwable ->
            emit(
                FeedUiState.Error(
                    message = throwable.localizedMessage,
                    messageRes = R.string.feature_feed_unknown_error,
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
            initialValue = FeedUiState.Loading,
        )

    fun selectLoaderType(type: ImageLoaderType) {
        _selectedLoaderType.value = type
    }
}
