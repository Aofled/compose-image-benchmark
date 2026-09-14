package ru.createsmart.composeimagebenchmark.core.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import ru.createsmart.composeimagebenchmark.core.data.di.IoDispatcher
import ru.createsmart.composeimagebenchmark.core.data.mapper.toDomain
import ru.createsmart.composeimagebenchmark.core.data.model.BenchmarkItemDto
import ru.createsmart.composeimagebenchmark.core.domain.repository.BenchmarkFeedRepository
import ru.createsmart.composeimagebenchmark.core.model.BenchmarkItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AssetBenchmarkFeedRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BenchmarkFeedRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val mutex = Mutex()
    private var cachedItems: List<BenchmarkItem>? = null

    override fun getFeedItems(itemCount: Int): Flow<List<BenchmarkItem>> = flow {
        val items = mutex.withLock {
            cachedItems ?: loadItemsFromAssets().also { cachedItems = it }
        }
        emit(items.take(itemCount))
    }.flowOn(ioDispatcher)

    private fun loadItemsFromAssets(): List<BenchmarkItem> {
        return context.assets.open("benchmark_feed.json").bufferedReader(Charsets.UTF_8).use { reader ->
            // Read text and remove BOM marker if present.
            val cleanJsonString = reader.readText().trimStart('\uFEFF')
            val dtos = json.decodeFromString<List<BenchmarkItemDto>>(cleanJsonString)
            dtos.map { it.toDomain() }
        }
    }
}
