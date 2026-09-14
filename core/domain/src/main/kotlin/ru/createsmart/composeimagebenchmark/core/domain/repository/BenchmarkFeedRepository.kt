package ru.createsmart.composeimagebenchmark.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.createsmart.composeimagebenchmark.core.model.BenchmarkItem

/**
 * Data provider contract for the heavy benchmark list.
 * Clean interface without implementation details.
 */
public interface BenchmarkFeedRepository {

    /**
     * Returns a reactive flow with a list of items.
     *
     * @param itemCount Number of items to generate (default is 500).
     */
    public fun getFeedItems(itemCount: Int = 500): Flow<List<BenchmarkItem>>
}
