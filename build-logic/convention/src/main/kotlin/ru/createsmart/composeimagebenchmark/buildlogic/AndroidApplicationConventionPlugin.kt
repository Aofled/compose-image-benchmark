package ru.createsmart.composeimagebenchmark.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.configureKotlinAndroid
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.findVersionInt
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.libs

/**
 * Configures the main App module (builds APK). Don't use in libraries.
 * Only sets up base Android/Kotlin configuration.
 * App-specific settings (applicationId, versionCode, buildTypes) are configured in the module itself.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("composeimagebenchmark.detekt")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                // Base SDK settings — same for all apps in the project
                defaultConfig {
                    targetSdk = libs.findVersionInt("targetSdk")
                }
            }
        }
    }
}
