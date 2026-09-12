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
}

gradlePlugin {
    plugins {
    }
}
