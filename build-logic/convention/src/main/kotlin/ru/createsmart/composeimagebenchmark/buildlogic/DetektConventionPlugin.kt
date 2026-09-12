package ru.createsmart.composeimagebenchmark.buildlogic

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import ru.createsmart.composeimagebenchmark.buildlogic.extensions.libs

/**
 * Detekt and Formatting configuration.
 */
class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            val isAutoCorrectEnabled = providers.gradleProperty("detekt.autocorrect").getOrElse("false") == "true"

            extensions.configure<DetektExtension>("detekt") {
                // Find detekt config file safely
                val configFile = rootProject.layout.projectDirectory.file("config/detekt/detekt.yml").asFile.let {
                    if (it.exists()) it else file("${rootProject.projectDir.parentFile}/config/detekt/detekt.yml")
                }

                config.setFrom(files(configFile))
                source.setFrom(files("src"))
                buildUponDefaultConfig = true
                parallel = true

                autoCorrect = isAutoCorrectEnabled
            }

            tasks.withType<Detekt>().configureEach {
                autoCorrect = isAutoCorrectEnabled
            }

            // The name detektAll matches the task name in the root build.gradle.kts.
            // This allows ./gradlew detektAll to automatically discover and run the check in this module.
            // Do not rename or delete without updating the root file; otherwise, the module will no longer be checked.
            tasks.register("detektAll") {
                group = "verification"
                description = "Runs detekt for this module"
                dependsOn(tasks.named("detekt"))
            }

            dependencies {
                add("detektPlugins", libs.findLibrary("detekt-formatting").get())
                add("detektPlugins", libs.findLibrary("detekt-compose-rules").get())
            }
        }
    }
}
