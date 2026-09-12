plugins {
    `kotlin-dsl`
}

group = "ru.createsmart.composeimagebenchmark.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlinComposeGradlePlugin)
    implementation(libs.kspGradlePlugin)
    implementation(libs.hiltGradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "composeimagebenchmark.android.application"
            implementationClass =
                "ru.createsmart.composeimagebenchmark.buildlogic.AndroidApplicationConventionPlugin"
        }
        
        register("androidApplicationCompose") {
            id = "composeimagebenchmark.android.application.compose"
            implementationClass =
                "ru.createsmart.composeimagebenchmark.buildlogic.AndroidApplicationComposeConventionPlugin"
        }

        register("androidLibrary") {
            id = "composeimagebenchmark.android.library"
            implementationClass =
                "ru.createsmart.composeimagebenchmark.buildlogic.AndroidLibraryConventionPlugin"
        }

        register("androidLibraryCompose") {
            id = "composeimagebenchmark.android.library.compose"
            implementationClass =
                "ru.createsmart.composeimagebenchmark.buildlogic.AndroidLibraryComposeConventionPlugin"
        }

        register("jvmLibrary") {
            id = "composeimagebenchmark.jvm.library"
            implementationClass = "ru.createsmart.composeimagebenchmark.buildlogic.JvmLibraryConventionPlugin"
        }

        register("hiltConvention") {
            id = "composeimagebenchmark.hilt"
            implementationClass = "ru.createsmart.composeimagebenchmark.buildlogic.HiltConventionPlugin"
        }
    }
}
