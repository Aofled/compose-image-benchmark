plugins {
    id("composeimagebenchmark.android.library")
    id("composeimagebenchmark.hilt")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)

    implementation(libs.coil.android)

    implementation(libs.bundles.coroutines)
    implementation(libs.kotlinx.serialization.json)
}
