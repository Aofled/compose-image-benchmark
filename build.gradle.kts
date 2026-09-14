// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.detekt) apply false
}

// To clean the application gradle module correctly (Build -> Clean Project or ./gradlew clean)
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// Root task to run Detekt on all modules and build-logic plugins.
tasks.register("detektAll") {
    group = "verification"
    description = "Run Detekt on all modules including build-logic"

    // Run detekt for build-logic using composite build.
    dependsOn(gradle.includedBuild("build-logic").task(":convention:detekt"))
}
