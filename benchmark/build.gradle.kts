plugins {
    id("composeimagebenchmark.android.benchmark")
}

android {
    defaultConfig {
        // Only for local debugging. Remove or ensure physical device execution before final metrics collection.
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR,LOW-BATTERY"
    }
}

dependencies {
    implementation(projects.core.model)
    // Required only for compile-time verified constants (BENCHMARK_FEED_LIST_TAG).
    // No UI code from :feature:feed is used here. Macrobenchmark still interacts via UiAutomator.
    implementation(projects.feature.feed)
}
