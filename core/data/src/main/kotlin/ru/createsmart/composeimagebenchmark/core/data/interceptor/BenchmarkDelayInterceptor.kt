package ru.createsmart.composeimagebenchmark.core.data.interceptor

import coil3.intercept.Interceptor
import coil3.request.ImageResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

/**
 * Coil 3 interceptor to simulate decoding/network delay.
 * Reads ?delay=XXX from URI, waits, and cleans URI for AssetManager.
 */
@Singleton
internal class BenchmarkDelayInterceptor @Inject constructor() : Interceptor {

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val dataString = request.data.toString()

        if (!dataString.contains("delay=")) {
            return chain.proceed()
        }

        val delayMs = dataString.substringAfter("delay=", "")
            .substringBefore("&")
            .toLongOrNull() ?: 0L

        if (delayMs > 0) {
            delay(delayMs.milliseconds)
        }

        // Remove query parameter to prevent AssetManager errors
        val cleanData = if (dataString.contains("?delay=")) {
            dataString.substringBefore("?delay=")
        } else {
            request.data
        }

        val sanitizedRequest = request.newBuilder()
            .data(cleanData)
            .build()

        return chain.withRequest(sanitizedRequest).proceed()
    }
}
