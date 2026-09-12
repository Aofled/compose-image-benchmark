plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.createsmart.composeimagebenchmark"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "ru.createsmart.composeimagebenchmark"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
