package ru.createsmart.composeimagebenchmark.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.configureKotlinAndroid

/**
 * Configures Library modules (Features, Core). No APK here.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                // Optimization: Disable unnecessary BuildConfig generation to speed up builds
                buildFeatures {
                    buildConfig = false
                }

                defaultConfig {
                    // How to prevent the "missing proguard file" error in AGP 8/9
                    val proguardFile = file("consumer-rules.pro")
                    if (proguardFile.exists()) {
                        consumerProguardFiles(proguardFile)
                    }
                }
            }
        }
    }
}
