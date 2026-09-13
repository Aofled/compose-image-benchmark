package ru.createsmart.composeimagebenchmark.buildlogic

import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.configureKotlinAndroid
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.findVersionInt
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.libs

/**
 * Configures Benchmark modules (Macrobenchmark) to measure performance
 */
class AndroidBenchmarkConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // Special plugin for Benchmark/Test modules (not App, not Library)
                apply("com.android.test")
                apply("composeimagebenchmark.detekt")
            }

            extensions.configure<TestExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    targetSdk = libs.findVersionInt("targetSdk")
                }

                targetProjectPath = ":app"

                buildTypes {
                    create("benchmark") {
                        isDebuggable = true
                        signingConfig = getByName("debug").signingConfig
                        matchingFallbacks += listOf("release")
                    }
                }

                experimentalProperties["android.experimental.self-instrumenting"] = true
            }

            // Build and run only "benchmark" variant.
            // Default "debug" and "release" variants are not used.
            extensions.configure<TestAndroidComponentsExtension> {
                beforeVariants(selector().all()) { variantBuilder ->
                    variantBuilder.enable = variantBuilder.buildType == "benchmark"
                }
            }

            dependencies {
                add("implementation", libs.findBundle("benchmark").get())
            }
        }
    }
}
