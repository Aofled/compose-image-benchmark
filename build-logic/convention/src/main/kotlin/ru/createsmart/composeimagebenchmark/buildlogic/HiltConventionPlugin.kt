package ru.createsmart.composeimagebenchmark.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.libs

/**
 * Configures Dependency Injection (Hilt + KSP)
 */
class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
            }

            dependencies {
                "implementation"(libs.findLibrary("hilt-android").get())
                "ksp"(libs.findLibrary("hilt-compiler").get())

                // Testing
                "androidTestImplementation"(libs.findLibrary("hilt-android-testing").get())
                "kspAndroidTest"(libs.findLibrary("hilt-compiler").get())

                "testImplementation"(libs.findLibrary("hilt-android-testing").get())
                "kspTest"(libs.findLibrary("hilt-compiler").get())
            }
        }
    }
}
