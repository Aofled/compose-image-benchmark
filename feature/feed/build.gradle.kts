plugins {
    id("composeimagebenchmark.android.library.compose")
    id("composeimagebenchmark.hilt")
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)

    implementation(libs.bundles.coil)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.bundles.coroutines)
}
