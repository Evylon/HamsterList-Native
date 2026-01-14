import com.android.build.api.dsl.androidLibrary

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.kotlin.multiplatform.library)
    kotlin("plugin.serialization") version libs.versions.kotlin
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.kmp.nativecoroutines)
}

kotlin {
    androidLibrary {
        namespace = "org.stratum0.hamsterlist"
        compileSdk = 36
        androidResources {
            enable = true
        }
    }
    jvmToolchain(21)

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
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlin.logging)
            implementation(libs.koin.core)
            api(libs.multiplatform.settings.no.arg)
            api(libs.multiplatform.settings.coroutines)
            api(libs.multiplatform.settings.make.observable)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.slf4j.android)
            api(libs.lifecycle.viewmodel.compose)
            api(libs.lifecycle.runtime.compose)
            api(libs.koin.android)
            api(libs.koin.androidx.compose)
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {}
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":HamsterListCore"))
}
