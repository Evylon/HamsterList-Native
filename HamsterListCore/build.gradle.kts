plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-25"
}

object Versions {
    const val KTOR = "2.3.12"
    const val COROUTINE = "1.8.1"
    const val KOIN = "3.5.6"
    const val LIFECYCLE = "2.8.6"
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "HamsterListCore"
        }
    }

    @Suppress("UnusedPrivateProperty")
    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}")
                implementation("io.ktor:ktor-client-core:${Versions.KTOR}")
                implementation("io.ktor:ktor-client-content-negotiation:${Versions.KTOR}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.KTOR}")
                implementation("io.ktor:ktor-client-logging:${Versions.KTOR}")
                implementation("io.github.oshai:kotlin-logging:5.1.4")
                implementation("io.insert-koin:koin-core:${Versions.KOIN}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:${Versions.KTOR}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINE}")
                implementation("org.slf4j:slf4j-android:1.7.36")
                api("androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.LIFECYCLE}")
                api("androidx.lifecycle:lifecycle-runtime-compose:${Versions.LIFECYCLE}")
                api("io.insert-koin:koin-android:${Versions.KOIN}")
                api("io.insert-koin:koin-androidx-compose:${Versions.KOIN}")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:${Versions.KTOR}")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {}
    }
}

android {
    namespace = "org.stratum0.hamsterlist"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
