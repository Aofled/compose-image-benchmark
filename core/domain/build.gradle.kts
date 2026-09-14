plugins {
    id("composeimagebenchmark.jvm.library")
}

dependencies {
    api(projects.core.model)
    // Async support. Use 'core' version, not 'android'.
    // Domain layer must NOT know about Android (Main Thread / Context).
    api(libs.kotlinx.coroutines.core)
    // Architecture: Standard Java annotations (@Inject).
    // Allows Dependency Injection without depending on the heavy Hilt library.
    implementation(libs.javax.inject)
}
