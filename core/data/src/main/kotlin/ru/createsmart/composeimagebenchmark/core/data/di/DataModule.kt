package ru.createsmart.composeimagebenchmark.core.data.di

import android.content.Context
import coil3.ImageLoader
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.createsmart.composeimagebenchmark.core.data.interceptor.BenchmarkDelayInterceptor
import ru.createsmart.composeimagebenchmark.core.data.repository.AssetBenchmarkFeedRepositoryImpl
import ru.createsmart.composeimagebenchmark.core.domain.repository.BenchmarkFeedRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    internal abstract fun bindBenchmarkFeedRepository(
        impl: AssetBenchmarkFeedRepositoryImpl,
    ): BenchmarkFeedRepository

    internal companion object {

        private const val MEMORY_CACHE_MAX_SIZE_BYTES =
            128L * 1024 * 1024 // 128 MB, fixed for all runs

        @Provides
        @Singleton
        internal fun provideImageLoader(
            @ApplicationContext context: Context,
            delayInterceptor: BenchmarkDelayInterceptor,
        ): ImageLoader {
            return ImageLoader.Builder(context)
                .components {
                    add(delayInterceptor)
                }
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizeBytes(MEMORY_CACHE_MAX_SIZE_BYTES)
                        .build()
                }
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build()
        }
    }
}
