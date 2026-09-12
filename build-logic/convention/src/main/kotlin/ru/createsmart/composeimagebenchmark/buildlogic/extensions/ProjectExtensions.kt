package ru.createsmart.composeimagebenchmark.buildlogic.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Access the `libs.versions.toml` directory from any plugin
 */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Reading the TOML version as Int (e.g. compileSdk, minSdk)
 */
internal fun VersionCatalog.findVersionInt(name: String): Int {
    return findVersion(name).get().requiredVersion.toInt()
}

/**
 * Returns the project path as a valid Kotlin package name (using dots).
 * Example: ":core:designsystem" -> "core.designsystem"
 * Example: ":app" -> "app"
 * Example: ":" -> "" (root project)
 */
internal val Project.pathAsNamespace: String
    get() = path.split(":")
        .filter { it.isNotEmpty() }
        .joinToString(".")
        .replace("-", "")

/**
 * Returns the project path as a valid Android resource prefix (using underscores).
 * Required for Library modules to avoid resource merge conflicts.
 * Example: ":core:designsystem" -> "core_designsystem_"
 * Example: ":app" -> "app_"
 * Example: ":" -> "" (root project — no prefix needed)
 */
internal val Project.pathAsResourcePrefix: String
    get() = path.split(":")
        .filter { it.isNotEmpty() }
        .joinToString("_")
        .replace("-", "_")
        .let { if (it.isNotEmpty()) "${it}_" else "" }
