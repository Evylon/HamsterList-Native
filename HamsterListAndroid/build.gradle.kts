import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.detekt)
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

val buildTag = System.getenv("GITHUB_RUN_NUMBER") ?: DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())!!

kotlin {
    jvmToolchain(21)
}

android {
    namespace = "org.stratum0.hamsterlist.android"
    compileSdk = 36
    defaultConfig {
        applicationId = "org.stratum0.hamsterlist.android"
        minSdk = 26
        targetSdk = 36
        versionCode = 24
        versionName = "1.1+$versionCode"
    }
    signingConfigs {
        register("app") {
            keyAlias = "hamsterList_release"
            keyPassword = System.getenv("SIGNING_PASSWORD") ?: ""
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("SIGNING_PASSWORD") ?: ""
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs["app"]
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config.from("detekt.yml")
}

dependencies {
    implementation(project(":HamsterListCore"))

    // Compose Bill of Materials
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.foundation)
    implementation(libs.material3)
    implementation(libs.material.icons.core)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
}
