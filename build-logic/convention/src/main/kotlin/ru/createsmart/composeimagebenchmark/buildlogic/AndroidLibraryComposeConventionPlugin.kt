package ru.createsmart.composeimagebenchmark.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.configureAndroidCompose

/**
 * Adds Compose support to Library modules (Core UI, Features)
 */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("composeimagebenchmark.android.library")

            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}
