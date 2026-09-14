package ru.createsmart.composeimagebenchmark.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.createsmart.composeimagebenchmark.core.domain.repository.BenchmarkFeedRepository
import ru.createsmart.composeimagebenchmark.core.model.BenchmarkItem
import javax.inject.Inject

/**
 * UseCase to get feed data.
 * Separates business logic from the ViewModel.
 */
public class GetBenchmarkFeedUseCase @Inject public constructor(
    private val repository: BenchmarkFeedRepository,
) {

    /**
     * Requests feed items with a specific pool size.
     */
    public operator fun invoke(itemCount: Int = 500): Flow<List<BenchmarkItem>> {
        return repository.getFeedItems(itemCount = itemCount)
    }
}
