package ru.createsmart.composeimagebenchmark.buildlogic.extensions

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Setup base Android settings for App and Library modules.
 * Applied by both AndroidApplicationConventionPlugin and AndroidLibraryConventionPlugin.
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        // Universal namespace generation — no hardcoded module names.
        // ":app" -> "ru.createsmart.composeimagebenchmark.app"
        // ":core:model" -> "ru.createsmart.composeimagebenchmark.core.model"
        val baseNamespace = "ru.createsmart.composeimagebenchmark"
        namespace = if (pathAsNamespace.isNotEmpty()) {
            "$baseNamespace.$pathAsNamespace"
        } else {
            baseNamespace
        }

        compileSdk = libs.findVersionInt("compileSdk")

        defaultConfig.apply {
            minSdk = libs.findVersionInt("minSdk")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        // Enforce resource prefix for Library modules to prevent resource name collisions
        // during merge. Not applicable to Application modules.
        if (this is LibraryExtension) {
            resourcePrefix = pathAsResourcePrefix
        }

        lint.apply {
            abortOnError = true
            checkDependencies = true
        }
    }

    extensions.configure<KotlinProjectExtension> {
        explicitApi = ExplicitApiMode.Strict
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            // Agreeing to use experimental coroutines throughout the project
            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}
