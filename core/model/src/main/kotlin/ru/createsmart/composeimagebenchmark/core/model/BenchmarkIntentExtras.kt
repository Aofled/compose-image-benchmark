package ru.createsmart.composeimagebenchmark.core.model

/**
 * Shared contract between :app and :benchmark for launching a specific strategy
 * without any UI interaction (a tap on the strategy menu would itself produce
 * frames that pollute the measurement).
 */
public object BenchmarkIntentExtras {
    public const val EXTRA_LOADER_TYPE: String = "extra_loader_type"
}
