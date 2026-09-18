plugins {
    id("composeimagebenchmark.android.application.compose")
    id("composeimagebenchmark.hilt")
}

android {
    defaultConfig {
        applicationId = "ru.createsmart.composeimagebenchmark"

        versionCode = providers.gradleProperty("APP_VERSION_CODE").map { it.toInt() }.getOrElse(1)
        versionName = providers.gradleProperty("APP_VERSION_NAME").getOrElse("1.0.0")
    }

    testBuildType = "benchmark"

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
            isProfileable = true
        }
    }
}

dependencies {
    // Feature + DI graph. :core:data is required here — it carries the Hilt modules
    // (DataModule, DispatchersModule) that satisfy bindings requested by :feature:feed.
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.feature.feed)

    implementation(libs.androidx.core.ktx)
}
