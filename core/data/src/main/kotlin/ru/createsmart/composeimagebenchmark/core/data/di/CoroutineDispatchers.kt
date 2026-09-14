package ru.createsmart.composeimagebenchmark.core.data.di

import javax.inject.Qualifier

/**
 * Hilt qualifier for Dispatchers.IO.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class IoDispatcher

/**
 * Hilt qualifier for Dispatchers.Default (for CPU-bound tasks).
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DefaultDispatcher
